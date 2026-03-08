package org.ecommerce.userauthenticationservice.repositories;

import org.ecommerce.userauthenticationservice.models.User;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.orm.jpa.DataJpaTest;

import java.util.Optional;

import static org.assertj.core.api.Assertions.assertThat;

@DataJpaTest
class UserRepositoryTest {

    @Autowired
    private UserRepository userRepository;

    private User buildUser(String email, String name, String password, String phoneNumber) {
        User user = new User();
        user.setEmail(email);
        user.setName(name);
        user.setPassword(password);
        user.setPhoneNumber(phoneNumber);
        return user;
    }

    // ========== findByEmail ==========

    @Nested
    @DisplayName("findByEmail")
    class FindByEmailTests {

        @Test
        @DisplayName("should return the user when the email exists")
        void findByEmail_ExistingEmail_ReturnsUser() {
            User saved = userRepository.save(buildUser("alice@example.com", "Alice", "encoded", "1234567890"));

            Optional<User> result = userRepository.findByEmail("alice@example.com");

            assertThat(result).isPresent();
            assertThat(result.get().getId()).isEqualTo(saved.getId());
            assertThat(result.get().getName()).isEqualTo("Alice");
            assertThat(result.get().getEmail()).isEqualTo("alice@example.com");
        }

        @Test
        @DisplayName("should return empty when the email does not exist")
        void findByEmail_NonExistingEmail_ReturnsEmpty() {
            Optional<User> result = userRepository.findByEmail("nonexistent@example.com");
            assertThat(result).isEmpty();
        }

        @Test
        @DisplayName("should not find a user when a different email is queried")
        void findByEmail_WrongEmail_ReturnsEmpty() {
            userRepository.save(buildUser("bob@example.com", "Bob", "encoded", "0000000000"));

            Optional<User> result = userRepository.findByEmail("alice@example.com");

            assertThat(result).isEmpty();
        }
    }

    // ========== save and findById ==========

    @Nested
    @DisplayName("save and findById")
    class SaveAndFindByIdTests {

        @Test
        @DisplayName("should persist a user and assign a generated id")
        void save_PersistsUserAndAssignsId() {
            User user = buildUser("carol@example.com", "Carol", "pass", "9999999999");
            User saved = userRepository.save(user);

            assertThat(saved.getId()).isNotNull().isPositive();
        }

        @Test
        @DisplayName("should retrieve a saved user by id")
        void findById_ReturnsSavedUser() {
            User saved = userRepository.save(buildUser("dave@example.com", "Dave", "pass", "1111111111"));

            Optional<User> result = userRepository.findById(saved.getId());

            assertThat(result).isPresent();
            assertThat(result.get().getEmail()).isEqualTo("dave@example.com");
            assertThat(result.get().getName()).isEqualTo("Dave");
        }

        @Test
        @DisplayName("should return empty when findById is called with an unknown id")
        void findById_UnknownId_ReturnsEmpty() {
            Optional<User> result = userRepository.findById(999_999L);
            assertThat(result).isEmpty();
        }
    }

    // ========== delete ==========

    @Nested
    @DisplayName("delete")
    class DeleteTests {

        @Test
        @DisplayName("should remove a user so it is no longer findable by id")
        void delete_RemovesUser() {
            User saved = userRepository.save(buildUser("eve@example.com", "Eve", "pass", "2222222222"));
            Long id = saved.getId();

            userRepository.deleteById(id);

            assertThat(userRepository.findById(id)).isEmpty();
        }
    }
}
