package org.ecommerce.userauthenticationservice.models;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

import java.util.Date;

import static org.assertj.core.api.Assertions.assertThat;

class RoleTest {

    @Test
    @DisplayName("should set and get value")
    void role_SetAndGetValue() {
        Role role = new Role();
        role.setValue("ROLE_USER");
        assertThat(role.getValue()).isEqualTo("ROLE_USER");
    }

    @Test
    @DisplayName("should support arbitrary role value strings")
    void role_SupportsArbitraryValues() {
        Role role = new Role();
        role.setValue("ROLE_ADMIN");
        assertThat(role.getValue()).isEqualTo("ROLE_ADMIN");
    }

    @Test
    @DisplayName("should inherit id, createdAt, lastUpdatedAt, status from BaseModel")
    void role_InheritsBaseModelFields() {
        Role role = new Role();
        Date now = new Date();

        role.setId(5L);
        role.setCreatedAt(now);
        role.setLastUpdatedAt(now);
        role.setStatus(Status.ACTIVE);

        assertThat(role.getId()).isEqualTo(5L);
        assertThat(role.getCreatedAt()).isEqualTo(now);
        assertThat(role.getLastUpdatedAt()).isEqualTo(now);
        assertThat(role.getStatus()).isEqualTo(Status.ACTIVE);
    }
}
