package org.ecommerce.userauthenticationservice.services;

import org.ecommerce.userauthenticationservice.models.User;
import org.ecommerce.userauthenticationservice.repositories.UserRepository;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.util.Optional;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
class UserServiceTest {

    @Mock
    private UserRepository userRepository;

    private UserService userService;

    @BeforeEach
    void setUp() {
        userService = new UserService(userRepository);
    }

    private User buildUser(Long id, String name, String email, String phoneNumber) {
        User user = new User();
        user.setId(id);
        user.setName(name);
        user.setEmail(email);
        user.setPhoneNumber(phoneNumber);
        return user;
    }

    @Nested
    @DisplayName("getUserDetails")
    class GetUserDetailsTests {

        @Test
        @DisplayName("should return user when user exists for the given id")
        void getUserDetails_UserExists_ReturnsUser() {
            // Arrange
            User user = buildUser(1L, "Alice", "alice@example.com", "9876543210");
            when(userRepository.findById(1L)).thenReturn(Optional.of(user));

            // Act
            User result = userService.getUserDetails(1L);

            // Assert
            assertThat(result).isNotNull();
            assertThat(result.getId()).isEqualTo(1L);
            assertThat(result.getName()).isEqualTo("Alice");
            assertThat(result.getEmail()).isEqualTo("alice@example.com");
            assertThat(result.getPhoneNumber()).isEqualTo("9876543210");
        }

        @Test
        @DisplayName("should return null when no user exists for the given id")
        void getUserDetails_UserNotFound_ReturnsNull() {
            // Arrange
            when(userRepository.findById(99L)).thenReturn(Optional.empty());

            // Act
            User result = userService.getUserDetails(99L);

            // Assert
            assertThat(result).isNull();
        }

        @Test
        @DisplayName("should delegate to repository with the exact id provided")
        void getUserDetails_CallsRepositoryWithCorrectId() {
            // Arrange
            Long id = 42L;
            User user = buildUser(id, "Bob", "bob@example.com", "0000000000");
            when(userRepository.findById(id)).thenReturn(Optional.of(user));

            // Act
            userService.getUserDetails(id);

            // Assert
            verify(userRepository).findById(42L);
        }

        @Test
        @DisplayName("should return the exact user object retrieved from the repository")
        void getUserDetails_ReturnsExactRepositoryObject() {
            // Arrange
            User expected = buildUser(5L, "Carol", "carol@example.com", "1111111111");
            when(userRepository.findById(5L)).thenReturn(Optional.of(expected));

            // Act
            User result = userService.getUserDetails(5L);

            // Assert
            assertThat(result).isSameAs(expected);
        }
    }
}
