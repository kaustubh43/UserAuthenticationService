package org.ecommerce.userauthenticationservice.services;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import io.jsonwebtoken.ExpiredJwtException;
import io.jsonwebtoken.Jwts;
import io.jsonwebtoken.security.MacAlgorithm;
import org.antlr.v4.runtime.misc.Pair;
import org.ecommerce.userauthenticationservice.clients.KafkaProducerClient;
import org.ecommerce.userauthenticationservice.exceptions.PasswordMismatchException;
import org.ecommerce.userauthenticationservice.exceptions.UserExistsException;
import org.ecommerce.userauthenticationservice.exceptions.UserNotRegisteredException;
import org.ecommerce.userauthenticationservice.models.Role;
import org.ecommerce.userauthenticationservice.models.Session;
import org.ecommerce.userauthenticationservice.models.Status;
import org.ecommerce.userauthenticationservice.models.User;
import org.ecommerce.userauthenticationservice.repositories.SessionRepository;
import org.ecommerce.userauthenticationservice.repositories.UserRepository;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.ArgumentCaptor;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.security.access.expression.SecurityExpressionHandler;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;

import javax.crypto.SecretKey;
import java.util.*;

import static org.assertj.core.api.Assertions.*;
import static org.mockito.ArgumentMatchers.*;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class AuthenticationServiceTest {

    @Mock
    private UserRepository userRepository;
    @Mock
    private BCryptPasswordEncoder passwordEncoder;
    @Mock
    private SessionRepository sessionRepository;
    @Mock
    private SecurityExpressionHandler<?> securityExpressionHandler;
    @Mock
    private KafkaProducerClient kafkaProducerClient;
    @Mock
    private ObjectMapper objectMapper;

    private SecretKey secretKey;
    private AuthenticationService authenticationService;

    @BeforeEach
    void setUp() {
        MacAlgorithm algorithm = Jwts.SIG.HS256;
        secretKey = algorithm.key().build();
        authenticationService = new AuthenticationService(
                userRepository,
                passwordEncoder,
                sessionRepository,
                secretKey,
                securityExpressionHandler,
                kafkaProducerClient,
                objectMapper
        );
    }

    // ========== Helper methods ==========

    private User buildUser(Long id, String email, String name, String password, String phoneNumber) {
        User user = new User();
        user.setId(id);
        user.setEmail(email);
        user.setName(name);
        user.setPassword(password);
        user.setPhoneNumber(phoneNumber);
        user.setRoles(new ArrayList<>());
        return user;
    }

    private String generateToken(Map<String, Object> claims) {
        return Jwts.builder().claims(claims).signWith(secretKey).compact();
    }

    // ========== SignUp Tests ==========

    @Nested
    @DisplayName("signUp")
    class SignUpTests {

        @Test
        @DisplayName("should create and return user when email is new")
        void signUp_NewEmail_ReturnsUser() throws JsonProcessingException {
            // Arrange
            String email = "test@example.com";
            String name = "Test User";
            String password = "rawPassword";
            String phoneNumber = "1234567890";

            when(userRepository.findByEmail(email)).thenReturn(Optional.empty());
            when(passwordEncoder.encode(password)).thenReturn("encodedPassword");
            when(userRepository.save(any(User.class))).thenAnswer(invocation -> {
                User saved = invocation.getArgument(0);
                saved.setId(1L);
                return saved;
            });
            when(objectMapper.writeValueAsString(any())).thenReturn("{\"email\":\"test\"}");

            // Act
            User result = authenticationService.signUp(email, name, password, phoneNumber);

            // Assert
            assertThat(result).isNotNull();
            assertThat(result.getEmail()).isEqualTo(email);
            assertThat(result.getName()).isEqualTo(name);
            assertThat(result.getPassword()).isEqualTo("encodedPassword");
            assertThat(result.getPhoneNumber()).isEqualTo(phoneNumber);

            verify(userRepository).findByEmail(email);
            verify(passwordEncoder).encode(password);
            verify(userRepository).save(any(User.class));
        }

        @Test
        @DisplayName("should send welcome email via Kafka on successful signup")
        void signUp_Success_SendsKafkaEmail() throws JsonProcessingException {
            // Arrange
            when(userRepository.findByEmail(anyString())).thenReturn(Optional.empty());
            when(passwordEncoder.encode(anyString())).thenReturn("encoded");
            when(userRepository.save(any(User.class))).thenAnswer(inv -> inv.getArgument(0));
            when(objectMapper.writeValueAsString(any())).thenReturn("{\"json\":\"value\"}");

            // Act
            authenticationService.signUp("user@test.com", "User", "pass", "9876543210");

            // Assert
            verify(kafkaProducerClient).sendMessage(eq("EMAIL_SIGNUP"), anyString());
            verify(objectMapper).writeValueAsString(any());
        }

        @Test
        @DisplayName("should throw UserExistsException when email already registered")
        void signUp_ExistingEmail_ThrowsUserExistsException() {
            // Arrange
            User existing = buildUser(1L, "existing@test.com", "Existing", "enc", "0000000000");
            when(userRepository.findByEmail("existing@test.com")).thenReturn(Optional.of(existing));

            // Act & Assert
            assertThatThrownBy(() ->
                    authenticationService.signUp("existing@test.com", "Name", "pass", "1111111111")
            )
                    .isInstanceOf(UserExistsException.class)
                    .hasMessageContaining("existing@test.com");

            verify(userRepository, never()).save(any());
            verify(kafkaProducerClient, never()).sendMessage(anyString(), anyString());
        }

        @Test
        @DisplayName("should throw RuntimeException when Kafka fails")
        void signUp_KafkaFails_ThrowsRuntimeException() throws JsonProcessingException {
            // Arrange
            when(userRepository.findByEmail(anyString())).thenReturn(Optional.empty());
            when(passwordEncoder.encode(anyString())).thenReturn("encoded");
            when(userRepository.save(any(User.class))).thenAnswer(inv -> inv.getArgument(0));
            when(objectMapper.writeValueAsString(any())).thenThrow(new JsonProcessingException("serialize error") {});

            // Act & Assert
            assertThatThrownBy(() ->
                    authenticationService.signUp("fail@test.com", "Fail", "pass", "0000000000")
            ).isInstanceOf(RuntimeException.class)
                    .hasMessageContaining("Failed while sending email");
        }

        @Test
        @DisplayName("should encode password before saving")
        void signUp_EncodesPassword() throws JsonProcessingException {
            // Arrange
            when(userRepository.findByEmail(anyString())).thenReturn(Optional.empty());
            when(passwordEncoder.encode("rawPassword")).thenReturn("$2a$10$hashedvalue");
            when(userRepository.save(any(User.class))).thenAnswer(inv -> inv.getArgument(0));
            when(objectMapper.writeValueAsString(any())).thenReturn("{}");

            // Act
            User result = authenticationService.signUp("pw@test.com", "PW User", "rawPassword", "1111111111");

            // Assert
            assertThat(result.getPassword()).isEqualTo("$2a$10$hashedvalue");
            verify(passwordEncoder).encode("rawPassword");
        }
    }

    // ========== Login Tests ==========

    @Nested
    @DisplayName("login")
    class LoginTests {

        @Test
        @DisplayName("should return user and JWT token on valid credentials")
        void login_ValidCredentials_ReturnsUserAndToken() {
            // Arrange
            User user = buildUser(1L, "user@test.com", "User", "encodedPass", "1234567890");
            Role role = new Role();
            role.setValue("ROLE_USER");
            user.setRoles(List.of(role));

            when(userRepository.findByEmail("user@test.com")).thenReturn(Optional.of(user));
            when(passwordEncoder.matches("rawPass", "encodedPass")).thenReturn(true);
            when(sessionRepository.save(any(Session.class))).thenAnswer(inv -> inv.getArgument(0));

            // Act
            Pair<User, String> result = authenticationService.login("user@test.com", "rawPass");

            // Assert
            assertThat(result.a).isEqualTo(user);
            assertThat(result.b).isNotBlank();

            verify(sessionRepository).save(any(Session.class));
        }

        @Test
        @DisplayName("should save session with ACTIVE status on login")
        void login_ValidCredentials_SavesActiveSession() {
            // Arrange
            User user = buildUser(1L, "user@test.com", "User", "encodedPass", "0000000000");
            when(userRepository.findByEmail("user@test.com")).thenReturn(Optional.of(user));
            when(passwordEncoder.matches("rawPass", "encodedPass")).thenReturn(true);
            when(sessionRepository.save(any(Session.class))).thenAnswer(inv -> inv.getArgument(0));

            // Act
            authenticationService.login("user@test.com", "rawPass");

            // Assert
            ArgumentCaptor<Session> sessionCaptor = ArgumentCaptor.forClass(Session.class);
            verify(sessionRepository).save(sessionCaptor.capture());
            Session savedSession = sessionCaptor.getValue();

            assertThat(savedSession.getUser()).isEqualTo(user);
            assertThat(savedSession.getToken()).isNotBlank();
            assertThat(savedSession.getStatus()).isEqualTo(Status.ACTIVE);
        }

        @Test
        @DisplayName("should throw UserNotRegisteredException for unknown email")
        void login_UnknownEmail_ThrowsUserNotRegisteredException() {
            // Arrange
            when(userRepository.findByEmail("unknown@test.com")).thenReturn(Optional.empty());

            // Act & Assert
            assertThatThrownBy(() ->
                    authenticationService.login("unknown@test.com", "pass")
            )
                    .isInstanceOf(UserNotRegisteredException.class)
                    .hasMessageContaining("unknown@test.com");

            verify(sessionRepository, never()).save(any());
        }

        @Test
        @DisplayName("should throw PasswordMismatchException for wrong password")
        void login_WrongPassword_ThrowsPasswordMismatchException() {
            // Arrange
            User user = buildUser(1L, "user@test.com", "User", "encodedPass", "0000000000");
            when(userRepository.findByEmail("user@test.com")).thenReturn(Optional.of(user));
            when(passwordEncoder.matches("wrongPass", "encodedPass")).thenReturn(false);

            // Act & Assert
            assertThatThrownBy(() ->
                    authenticationService.login("user@test.com", "wrongPass")
            )
                    .isInstanceOf(PasswordMismatchException.class)
                    .hasMessageContaining("user@test.com");

            verify(sessionRepository, never()).save(any());
        }

        @Test
        @DisplayName("should generate a JWT token that contains expected claims")
        void login_ValidCredentials_TokenContainsExpectedClaims() {
            // Arrange
            User user = buildUser(42L, "claims@test.com", "Claims User", "encoded", "0000000000");
            user.setRoles(new ArrayList<>());
            when(userRepository.findByEmail("claims@test.com")).thenReturn(Optional.of(user));
            when(passwordEncoder.matches("pass", "encoded")).thenReturn(true);
            when(sessionRepository.save(any(Session.class))).thenAnswer(inv -> inv.getArgument(0));

            // Act
            Pair<User, String> result = authenticationService.login("claims@test.com", "pass");

            // Assert – parse token back and verify claims
            var claims = Jwts.parser().verifyWith(secretKey).build()
                    .parseSignedClaims(result.b).getPayload();

            assertThat(claims.get("user_id", Long.class)).isEqualTo(42L);
            assertThat(claims.get("issued_by", String.class)).isEqualTo("ecommerce-app");
            assertThat(claims.get("iat")).isNotNull();
            assertThat(claims.get("exp")).isNotNull();
        }
    }

    // ========== ValidateToken Tests ==========

    @Nested
    @DisplayName("validateToken")
    class ValidateTokenTests {

        @Test
        @DisplayName("should return true for a valid non-expired token with matching session")
        void validateToken_ValidToken_ReturnsTrue() {
            // Arrange
            long now = System.currentTimeMillis() / 1000;
            Map<String, Object> claims = new HashMap<>();
            claims.put("user_id", 1L);
            claims.put("iat", now);
            claims.put("exp", now + 3600); // 1 hour from now
            claims.put("roles", List.of());
            claims.put("issued_by", "ecommerce-app");

            String token = generateToken(claims);

            Session session = new Session();
            session.setToken(token);
            session.setStatus(Status.ACTIVE);

            when(sessionRepository.findByTokenAndUser_Id(token, 1L)).thenReturn(Optional.of(session));

            // Act
            boolean result = authenticationService.validateToken(token, 1L);

            // Assert
            assertThat(result).isTrue();
            // Session status should not be changed
            verify(sessionRepository, never()).save(any());
        }

        @Test
        @DisplayName("should return false when session not found for token and userId")
        void validateToken_NoSession_ReturnsFalse() {
            // Arrange
            when(sessionRepository.findByTokenAndUser_Id("some-token", 99L)).thenReturn(Optional.empty());

            // Act
            boolean result = authenticationService.validateToken("some-token", 99L);

            // Assert
            assertThat(result).isFalse();
            verify(sessionRepository, never()).save(any());
        }

        @Test
        @DisplayName("should throw ExpiredJwtException when token is expired")
        void validateToken_ExpiredToken_ThrowsExpiredJwtException() {
            // Arrange – create a token that expired 1 hour ago
            long now = System.currentTimeMillis() / 1000;
            Map<String, Object> claims = new HashMap<>();
            claims.put("user_id", 1L);
            claims.put("iat", now - 7200);
            claims.put("exp", now - 3600); // expired 1 hour ago
            claims.put("roles", List.of());
            claims.put("issued_by", "ecommerce-app");

            // In JJWT 0.12.x, including "exp" in the claims map activates standard
            // JWT expiry validation at parse time. The parser throws ExpiredJwtException
            // before the service's custom expiry logic is reached.
            String token = Jwts.builder()
                    .claims(claims)
                    .signWith(secretKey)
                    .compact();

            Session session = new Session();
            session.setToken(token);
            session.setStatus(Status.ACTIVE);

            when(sessionRepository.findByTokenAndUser_Id(token, 1L)).thenReturn(Optional.of(session));

            // Act & Assert
            assertThatThrownBy(() -> authenticationService.validateToken(token, 1L))
                    .isInstanceOf(ExpiredJwtException.class);

            verify(sessionRepository, never()).save(any());
        }
    }
}

