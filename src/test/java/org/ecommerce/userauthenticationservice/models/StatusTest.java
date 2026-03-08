package org.ecommerce.userauthenticationservice.models;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;

class StatusTest {

    @Test
    @DisplayName("should define exactly two constants: ACTIVE and INACTIVE")
    void status_HasExactlyTwoValues() {
        assertThat(Status.values()).hasSize(2);
    }

    @Test
    @DisplayName("ACTIVE constant should exist and be resolvable by name")
    void status_ActiveExists() {
        assertThat(Status.valueOf("ACTIVE")).isEqualTo(Status.ACTIVE);
    }

    @Test
    @DisplayName("INACTIVE constant should exist and be resolvable by name")
    void status_InactiveExists() {
        assertThat(Status.valueOf("INACTIVE")).isEqualTo(Status.INACTIVE);
    }

    @Test
    @DisplayName("ACTIVE and INACTIVE should be distinct")
    void status_ActiveAndInactiveAreDistinct() {
        assertThat(Status.ACTIVE).isNotEqualTo(Status.INACTIVE);
    }

    @Test
    @DisplayName("should throw IllegalArgumentException for an unknown status name")
    void status_UnknownNameThrowsException() {
        assertThatThrownBy(() -> Status.valueOf("UNKNOWN"))
                .isInstanceOf(IllegalArgumentException.class);
    }
}
