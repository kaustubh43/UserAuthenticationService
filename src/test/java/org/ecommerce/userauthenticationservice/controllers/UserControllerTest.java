package org.ecommerce.userauthenticationservice.controllers;

import org.ecommerce.userauthenticationservice.models.Role;
import org.ecommerce.userauthenticationservice.models.User;
import org.ecommerce.userauthenticationservice.services.UserService;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.setup.MockMvcBuilders;

import java.util.ArrayList;
import java.util.List;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@ExtendWith(MockitoExtension.class)
class UserControllerTest {

    @Mock
    private UserService userService;

    private MockMvc mockMvc;

    @BeforeEach
    void setUp() {
        UserController userController = new UserController(userService);
        mockMvc = MockMvcBuilders.standaloneSetup(userController).build();
    }

    private User buildUser(Long id, String name, String email, String phoneNumber) {
        User user = new User();
        user.setId(id);
        user.setName(name);
        user.setEmail(email);
        user.setPhoneNumber(phoneNumber);
        user.setRoles(new ArrayList<>());
        return user;
    }

    // ========== GET /users/{id} Tests ==========

    @Nested
    @DisplayName("GET /users/{id}")
    class GetUserTests {

        @Test
        @DisplayName("should return 200 with UserDto when user exists")
        void getUser_ExistingId_ReturnsUserDto() throws Exception {
            // Arrange
            User user = buildUser(1L, "Alice", "alice@example.com", "9876543210");
            when(userService.getUserDetails(1L)).thenReturn(user);

            // Act & Assert
            mockMvc.perform(get("/users/1"))
                    .andExpect(status().isOk())
                    .andExpect(jsonPath("$.id").value(1))
                    .andExpect(jsonPath("$.name").value("Alice"))
                    .andExpect(jsonPath("$.email").value("alice@example.com"))
                    .andExpect(jsonPath("$.phoneNumber").value("9876543210"));
        }

        @Test
        @DisplayName("should throw NullPointerException when user is not found (no null guard in controller)")
        void getUser_NonExistingId_ThrowsNullPointerException() throws Exception {
            // Arrange
            when(userService.getUserDetails(99L)).thenReturn(null);

            // Act & Assert
            // The controller calls user.getName() without a null check.
            // MockMvc rethrows the NPE from perform() itself, so we assert on the thrown cause.
            assertThatThrownBy(() -> mockMvc.perform(get("/users/99")))
                    .hasRootCauseInstanceOf(NullPointerException.class);
        }

        @Test
        @DisplayName("should return user with populated roles list")
        void getUser_UserWithRoles_ReturnsRolesInDto() throws Exception {
            // Arrange
            Role role = new Role();
            role.setValue("ROLE_USER");
            User user = buildUser(2L, "Bob", "bob@example.com", "0000000000");
            user.setRoles(List.of(role));
            when(userService.getUserDetails(2L)).thenReturn(user);

            // Act & Assert
            mockMvc.perform(get("/users/2"))
                    .andExpect(status().isOk())
                    .andExpect(jsonPath("$.roles[0].value").value("ROLE_USER"));
        }

        @Test
        @DisplayName("should delegate to UserService with the correct id from the path")
        void getUser_CallsServiceWithCorrectId() throws Exception {
            // Arrange
            User user = buildUser(7L, "Dave", "dave@example.com", "5555555555");
            when(userService.getUserDetails(7L)).thenReturn(user);

            // Act
            mockMvc.perform(get("/users/7"));

            // Assert
            verify(userService).getUserDetails(7L);
        }
    }
}
