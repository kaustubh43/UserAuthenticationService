package org.ecommerce.userauthenticationservice.repositories;

import org.ecommerce.userauthenticationservice.models.Session;
import org.ecommerce.userauthenticationservice.models.Status;
import org.ecommerce.userauthenticationservice.models.User;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.orm.jpa.DataJpaTest;

import java.util.Optional;

import static org.assertj.core.api.Assertions.assertThat;

@DataJpaTest
class SessionRepositoryTest {

    @Autowired
    private SessionRepository sessionRepository;

    @Autowired
    private UserRepository userRepository;

    private User savedUser;

    @BeforeEach
    void setUp() {
        User user = new User();
        user.setEmail("alice@example.com");
        user.setName("Alice");
        user.setPassword("encodedPassword");
        user.setPhoneNumber("1234567890");
        savedUser = userRepository.save(user);
    }

    private Session buildSession(String token, User user, Status status) {
        Session session = new Session();
        session.setToken(token);
        session.setUser(user);
        session.setStatus(status);
        return session;
    }

    // ========== findByTokenAndUser_Id ==========

    @Nested
    @DisplayName("findByTokenAndUser_Id")
    class FindByTokenAndUserIdTests {

        @Test
        @DisplayName("should return session when both token and userId match")
        void findByTokenAndUser_Id_CorrectTokenAndUserId_ReturnsSession() {
            Session saved = sessionRepository.save(
                    buildSession("valid-token", savedUser, Status.ACTIVE));

            Optional<Session> result = sessionRepository.findByTokenAndUser_Id("valid-token", savedUser.getId());

            assertThat(result).isPresent();
            assertThat(result.get().getId()).isEqualTo(saved.getId());
            assertThat(result.get().getToken()).isEqualTo("valid-token");
            assertThat(result.get().getUser().getId()).isEqualTo(savedUser.getId());
        }

        @Test
        @DisplayName("should return empty when token does not match")
        void findByTokenAndUser_Id_WrongToken_ReturnsEmpty() {
            sessionRepository.save(buildSession("correct-token", savedUser, Status.ACTIVE));

            Optional<Session> result = sessionRepository.findByTokenAndUser_Id("wrong-token", savedUser.getId());

            assertThat(result).isEmpty();
        }

        @Test
        @DisplayName("should return empty when userId does not match")
        void findByTokenAndUser_Id_WrongUserId_ReturnsEmpty() {
            sessionRepository.save(buildSession("my-token", savedUser, Status.ACTIVE));

            Optional<Session> result = sessionRepository.findByTokenAndUser_Id("my-token", 999_999L);

            assertThat(result).isEmpty();
        }

        @Test
        @DisplayName("should return empty when no sessions exist")
        void findByTokenAndUser_Id_NoSessions_ReturnsEmpty() {
            Optional<Session> result = sessionRepository.findByTokenAndUser_Id("any-token", savedUser.getId());
            assertThat(result).isEmpty();
        }
    }

    // ========== save and findById ==========

    @Nested
    @DisplayName("save and findById")
    class SaveTests {

        @Test
        @DisplayName("should persist a session and assign a generated id")
        void save_PersistsSessionAndAssignsId() {
            Session session = buildSession("new-token", savedUser, Status.ACTIVE);
            Session saved = sessionRepository.save(session);

            assertThat(saved.getId()).isNotNull().isPositive();
        }

        @Test
        @DisplayName("should persist session status and allow it to be updated")
        void save_UpdatesSessionStatus() {
            Session session = buildSession("status-token", savedUser, Status.ACTIVE);
            Session saved = sessionRepository.save(session);

            saved.setStatus(Status.INACTIVE);
            Session updated = sessionRepository.save(saved);

            assertThat(updated.getStatus()).isEqualTo(Status.INACTIVE);
        }
    }
}
