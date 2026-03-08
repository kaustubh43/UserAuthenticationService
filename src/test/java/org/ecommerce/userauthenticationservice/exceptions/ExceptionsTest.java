package org.ecommerce.userauthenticationservice.exceptions;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;

class ExceptionsTest {

    // ========== UserExistsException ==========

    @Nested
    @DisplayName("UserExistsException")
    class UserExistsExceptionTests {

        @Test
        @DisplayName("should be a RuntimeException")
        void userExistsException_IsRuntimeException() {
            UserExistsException ex = new UserExistsException("user@example.com");
            assertThat(ex).isInstanceOf(RuntimeException.class);
        }

        @Test
        @DisplayName("should store the provided message")
        void userExistsException_StoresMessage() {
            UserExistsException ex = new UserExistsException("user@example.com");
            assertThat(ex.getMessage()).isEqualTo("user@example.com");
        }

        @Test
        @DisplayName("should be throwable and catchable as RuntimeException")
        void userExistsException_IsThrowableAndCatchable() {
            assertThatThrownBy(() -> {
                throw new UserExistsException("existing@example.com");
            })
                    .isInstanceOf(RuntimeException.class)
                    .hasMessage("existing@example.com");
        }
    }

    // ========== UserNotRegisteredException ==========

    @Nested
    @DisplayName("UserNotRegisteredException")
    class UserNotRegisteredExceptionTests {

        @Test
        @DisplayName("should be a RuntimeException")
        void userNotRegisteredException_IsRuntimeException() {
            UserNotRegisteredException ex = new UserNotRegisteredException("unknown@example.com");
            assertThat(ex).isInstanceOf(RuntimeException.class);
        }

        @Test
        @DisplayName("should store the provided message")
        void userNotRegisteredException_StoresMessage() {
            UserNotRegisteredException ex = new UserNotRegisteredException("unknown@example.com");
            assertThat(ex.getMessage()).isEqualTo("unknown@example.com");
        }

        @Test
        @DisplayName("should be throwable and catchable as RuntimeException")
        void userNotRegisteredException_IsThrowableAndCatchable() {
            assertThatThrownBy(() -> {
                throw new UserNotRegisteredException("ghost@example.com");
            })
                    .isInstanceOf(RuntimeException.class)
                    .hasMessage("ghost@example.com");
        }
    }

    // ========== PasswordMismatchException ==========

    @Nested
    @DisplayName("PasswordMismatchException")
    class PasswordMismatchExceptionTests {

        @Test
        @DisplayName("should be a RuntimeException")
        void passwordMismatchException_IsRuntimeException() {
            PasswordMismatchException ex = new PasswordMismatchException("user@example.com");
            assertThat(ex).isInstanceOf(RuntimeException.class);
        }

        @Test
        @DisplayName("should store the provided message")
        void passwordMismatchException_StoresMessage() {
            PasswordMismatchException ex = new PasswordMismatchException("user@example.com");
            assertThat(ex.getMessage()).isEqualTo("user@example.com");
        }

        @Test
        @DisplayName("should be throwable and catchable as RuntimeException")
        void passwordMismatchException_IsThrowableAndCatchable() {
            assertThatThrownBy(() -> {
                throw new PasswordMismatchException("badpass@example.com");
            })
                    .isInstanceOf(RuntimeException.class)
                    .hasMessage("badpass@example.com");
        }
    }
}
