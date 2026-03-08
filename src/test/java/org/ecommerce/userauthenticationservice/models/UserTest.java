package org.ecommerce.userauthenticationservice.models;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;

import java.util.Date;

import static org.assertj.core.api.Assertions.assertThat;

class UserTest {

    // ========== BaseModel inherited fields ==========

    @Nested
    @DisplayName("BaseModel inherited fields")
    class BaseModelFieldsTests {

        @Test
        @DisplayName("should set and get id")
        void user_SetAndGetId() {
            User user = new User();
            user.setId(10L);
            assertThat(user.getId()).isEqualTo(10L);
        }

        @Test
        @DisplayName("should set and get createdAt")
        void user_SetAndGetCreatedAt() {
            User user = new User();
            Date now = new Date();
            user.setCreatedAt(now);
            assertThat(user.getCreatedAt()).isEqualTo(now);
        }

        @Test
        @DisplayName("should set and get lastUpdatedAt")
        void user_SetAndGetLastUpdatedAt() {
            User user = new User();
            Date now = new Date();
            user.setLastUpdatedAt(now);
            assertThat(user.getLastUpdatedAt()).isEqualTo(now);
        }

        @Test
        @DisplayName("should set and get status")
        void user_SetAndGetStatus() {
            User user = new User();
            user.setStatus(Status.ACTIVE);
            assertThat(user.getStatus()).isEqualTo(Status.ACTIVE);

            user.setStatus(Status.INACTIVE);
            assertThat(user.getStatus()).isEqualTo(Status.INACTIVE);
        }
    }

    // ========== User-specific fields ==========

    @Nested
    @DisplayName("User fields")
    class UserFieldsTests {

        @Test
        @DisplayName("should set and get name")
        void user_SetAndGetName() {
            User user = new User();
            user.setName("Alice");
            assertThat(user.getName()).isEqualTo("Alice");
        }

        @Test
        @DisplayName("should set and get email")
        void user_SetAndGetEmail() {
            User user = new User();
            user.setEmail("alice@example.com");
            assertThat(user.getEmail()).isEqualTo("alice@example.com");
        }

        @Test
        @DisplayName("should set and get password")
        void user_SetAndGetPassword() {
            User user = new User();
            user.setPassword("encodedPassword");
            assertThat(user.getPassword()).isEqualTo("encodedPassword");
        }

        @Test
        @DisplayName("should set and get phoneNumber")
        void user_SetAndGetPhoneNumber() {
            User user = new User();
            user.setPhoneNumber("9876543210");
            assertThat(user.getPhoneNumber()).isEqualTo("9876543210");
        }

        @Test
        @DisplayName("should initialise roles to an empty list on construction")
        void user_RolesInitialisedToEmptyList() {
            User user = new User();
            assertThat(user.getRoles()).isNotNull().isEmpty();
        }

        @Test
        @DisplayName("should allow roles to be replaced")
        void user_SetRoles() {
            User user = new User();
            Role role = new Role();
            role.setValue("ROLE_USER");
            user.getRoles().add(role);
            assertThat(user.getRoles()).hasSize(1);
            assertThat(user.getRoles().get(0).getValue()).isEqualTo("ROLE_USER");
        }
    }
}
