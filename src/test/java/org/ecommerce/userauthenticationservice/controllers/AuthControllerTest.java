package org.ecommerce.userauthenticationservice.controllers;

import com.fasterxml.jackson.databind.ObjectMapper;
import org.antlr.v4.runtime.misc.Pair;
import org.ecommerce.userauthenticationservice.dtos.LoginRequestDto;
import org.ecommerce.userauthenticationservice.dtos.SignUpRequestDto;
import org.ecommerce.userauthenticationservice.dtos.ValidateTokenRequestDto;
import org.ecommerce.userauthenticationservice.exceptions.PasswordMismatchException;
import org.ecommerce.userauthenticationservice.exceptions.UserExistsException;
import org.ecommerce.userauthenticationservice.exceptions.UserNotRegisteredException;
import org.ecommerce.userauthenticationservice.models.Role;
import org.ecommerce.userauthenticationservice.models.User;
import org.ecommerce.userauthenticationservice.services.IAuthenticationService;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.setup.MockMvcBuilders;

import java.util.ArrayList;
import java.util.List;

import static org.mockito.ArgumentMatchers.anyString;
import static org.mockito.Mockito.when;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

@ExtendWith(MockitoExtension.class)
class AuthControllerTest {

    @Mock
    private IAuthenticationService authenticationService;

    private MockMvc mockMvc;
    private ObjectMapper objectMapper;

    @BeforeEach
    void setUp() {
        AuthController authController = new AuthController(authenticationService);
        mockMvc = MockMvcBuilders.standaloneSetup(authController).build();
        objectMapper = new ObjectMapper();
    }

    private User buildUser(Long id, String email, String name, String phoneNumber) {
        User user = new User();
        user.setId(id);
        user.setEmail(email);
        user.setName(name);
        user.setPhoneNumber(phoneNumber);
        user.setRoles(new ArrayList<>());
        return user;
    }

    // ========== SignUp Tests ==========

    @Nested
    @DisplayName("POST /auth/signup")
    class SignUpTests {

        @Test
        @DisplayName("should return 201 with UserDto on successful signup")
        void signUp_ValidRequest_Returns201AndUserDto() throws Exception {
            // Arrange
            SignUpRequestDto request = new SignUpRequestDto();
            request.setEmail("new@example.com");
            request.setName("New User");
            request.setPassword("password");
            request.setPhoneNumber("1234567890");

            User savedUser = buildUser(1L, "new@example.com", "New User", "1234567890");
            when(authenticationService.signUp("new@example.com", "New User", "password", "1234567890"))
                    .thenReturn(savedUser);

            // Act & Assert
            mockMvc.perform(post("/auth/signup")
                            .contentType(MediaType.APPLICATION_JSON)
                            .content(objectMapper.writeValueAsString(request)))
                    .andExpect(status().isCreated())
                    .andExpect(jsonPath("$.id").value(1))
                    .andExpect(jsonPath("$.email").value("new@example.com"))
                    .andExpect(jsonPath("$.name").value("New User"))
                    .andExpect(jsonPath("$.phoneNumber").value("1234567890"));
        }

        @Test
        @DisplayName("should return 400 when email is already registered")
        void signUp_ExistingEmail_Returns400() throws Exception {
            // Arrange
            SignUpRequestDto request = new SignUpRequestDto();
            request.setEmail("exists@example.com");
            request.setName("User");
            request.setPassword("password");
            request.setPhoneNumber("1234567890");

            when(authenticationService.signUp(anyString(), anyString(), anyString(), anyString()))
                    .thenThrow(new UserExistsException("exists@example.com"));

            // Act & Assert
            mockMvc.perform(post("/auth/signup")
                            .contentType(MediaType.APPLICATION_JSON)
                            .content(objectMapper.writeValueAsString(request)))
                    .andExpect(status().isBadRequest());
        }

        @Test
        @DisplayName("should return 400 when service throws any RuntimeException")
        void signUp_ServiceThrowsRuntimeException_Returns400() throws Exception {
            // Arrange
            SignUpRequestDto request = new SignUpRequestDto();
            request.setEmail("fail@example.com");
            request.setName("Fail User");
            request.setPassword("password");
            request.setPhoneNumber("1234567890");

            when(authenticationService.signUp(anyString(), anyString(), anyString(), anyString()))
                    .thenThrow(new RuntimeException("Failed while sending email via Kafka"));

            // Act & Assert
            mockMvc.perform(post("/auth/signup")
                            .contentType(MediaType.APPLICATION_JSON)
                            .content(objectMapper.writeValueAsString(request)))
                    .andExpect(status().isBadRequest());
        }

        @Test
        @DisplayName("should return null body on a failed signup")
        void signUp_ServiceFails_ReturnsNullBody() throws Exception {
            // Arrange
            SignUpRequestDto request = new SignUpRequestDto();
            request.setEmail("bad@example.com");
            request.setName("Bad User");
            request.setPassword("password");
            request.setPhoneNumber("0000000000");

            when(authenticationService.signUp(anyString(), anyString(), anyString(), anyString()))
                    .thenThrow(new RuntimeException("error"));

            // Act & Assert
            mockMvc.perform(post("/auth/signup")
                            .contentType(MediaType.APPLICATION_JSON)
                            .content(objectMapper.writeValueAsString(request)))
                    .andExpect(status().isBadRequest())
                    .andExpect(content().string(""));
        }
    }

    // ========== Login Tests ==========

    @Nested
    @DisplayName("POST /auth/login")
    class LoginTests {

        @Test
        @DisplayName("should return 200 with UserDto and Set-Cookie header on valid credentials")
        void login_ValidCredentials_Returns200AndSetsCookieHeader() throws Exception {
            // Arrange
            LoginRequestDto request = new LoginRequestDto();
            request.setEmail("user@example.com");
            request.setPassword("password");

            User user = buildUser(1L, "user@example.com", "User", "1234567890");
            // Set-Cookie header requires name=value format; raw JWT strings are not valid
            String token = "auth=valid-token-value";
            when(authenticationService.login("user@example.com", "password"))
                    .thenReturn(new Pair<>(user, token));

            // Act & Assert
            mockMvc.perform(post("/auth/login")
                            .contentType(MediaType.APPLICATION_JSON)
                            .content(objectMapper.writeValueAsString(request)))
                    .andExpect(status().isOk())
                    .andExpect(header().string("Set-Cookie", token))
                    .andExpect(jsonPath("$.id").value(1))
                    .andExpect(jsonPath("$.email").value("user@example.com"));
        }

        @Test
        @DisplayName("should return 401 when password does not match")
        void login_PasswordMismatch_Returns401() throws Exception {
            // Arrange
            LoginRequestDto request = new LoginRequestDto();
            request.setEmail("user@example.com");
            request.setPassword("wrongPassword");

            when(authenticationService.login(anyString(), anyString()))
                    .thenThrow(new PasswordMismatchException("user@example.com"));

            // Act & Assert
            mockMvc.perform(post("/auth/login")
                            .contentType(MediaType.APPLICATION_JSON)
                            .content(objectMapper.writeValueAsString(request)))
                    .andExpect(status().isUnauthorized());
        }

        @Test
        @DisplayName("should return 400 when user email is not registered")
        void login_UserNotRegistered_Returns400() throws Exception {
            // Arrange
            LoginRequestDto request = new LoginRequestDto();
            request.setEmail("ghost@example.com");
            request.setPassword("password");

            when(authenticationService.login(anyString(), anyString()))
                    .thenThrow(new UserNotRegisteredException("ghost@example.com"));

            // Act & Assert
            mockMvc.perform(post("/auth/login")
                            .contentType(MediaType.APPLICATION_JSON)
                            .content(objectMapper.writeValueAsString(request)))
                    .andExpect(status().isBadRequest());
        }

        @Test
        @DisplayName("should return user with roles in the response body")
        void login_UserWithRoles_ReturnsRolesInBody() throws Exception {
            // Arrange
            LoginRequestDto request = new LoginRequestDto();
            request.setEmail("admin@example.com");
            request.setPassword("password");

            Role role = new Role();
            role.setValue("ROLE_ADMIN");
            User user = buildUser(2L, "admin@example.com", "Admin", "0000000000");
            user.setRoles(List.of(role));

            when(authenticationService.login("admin@example.com", "password"))
                    .thenReturn(new Pair<>(user, "auth=some-token"));

            // Act & Assert
            mockMvc.perform(post("/auth/login")
                            .contentType(MediaType.APPLICATION_JSON)
                            .content(objectMapper.writeValueAsString(request)))
                    .andExpect(status().isOk())
                    .andExpect(jsonPath("$.roles[0].value").value("ROLE_ADMIN"));
        }
    }

    // ========== ValidateToken Tests ==========

    @Nested
    @DisplayName("POST /auth/validate-token")
    class ValidateTokenTests {

        @Test
        @DisplayName("should return 200 with success message when token is valid")
        void validateToken_ValidToken_Returns200WithMessage() throws Exception {
            // Arrange
            ValidateTokenRequestDto request = new ValidateTokenRequestDto();
            request.setToken("valid-jwt-token");
            request.setUserId(1L);

            when(authenticationService.validateToken("valid-jwt-token", 1L)).thenReturn(true);

            // Act & Assert
            mockMvc.perform(post("/auth/validate-token")
                            .contentType(MediaType.APPLICATION_JSON)
                            .content(objectMapper.writeValueAsString(request)))
                    .andExpect(status().isOk())
                    .andExpect(content().string("Token valid and within expiry"));
        }

        @Test
        @DisplayName("should return 401 with invalid message when token is not valid")
        void validateToken_InvalidToken_Returns401WithMessage() throws Exception {
            // Arrange
            ValidateTokenRequestDto request = new ValidateTokenRequestDto();
            request.setToken("expired-or-invalid-token");
            request.setUserId(1L);

            when(authenticationService.validateToken("expired-or-invalid-token", 1L)).thenReturn(false);

            // Act & Assert
            mockMvc.perform(post("/auth/validate-token")
                            .contentType(MediaType.APPLICATION_JSON)
                            .content(objectMapper.writeValueAsString(request)))
                    .andExpect(status().isUnauthorized())
                    .andExpect(content().string("Token is not valid."));
        }

        @Test
        @DisplayName("should return 401 when session does not exist for user")
        void validateToken_NoSession_Returns401() throws Exception {
            // Arrange
            ValidateTokenRequestDto request = new ValidateTokenRequestDto();
            request.setToken("some-token");
            request.setUserId(99L);

            when(authenticationService.validateToken("some-token", 99L)).thenReturn(false);

            // Act & Assert
            mockMvc.perform(post("/auth/validate-token")
                            .contentType(MediaType.APPLICATION_JSON)
                            .content(objectMapper.writeValueAsString(request)))
                    .andExpect(status().isUnauthorized())
                    .andExpect(content().string("Token is not valid."));
        }
    }
}
