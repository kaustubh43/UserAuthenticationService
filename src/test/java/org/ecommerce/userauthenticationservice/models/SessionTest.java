package org.ecommerce.userauthenticationservice.models;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

import java.util.Date;

import static org.assertj.core.api.Assertions.assertThat;

class SessionTest {

    @Test
    @DisplayName("should set and get token")
    void session_SetAndGetToken() {
        Session session = new Session();
        session.setToken("jwt.token.value");
        assertThat(session.getToken()).isEqualTo("jwt.token.value");
    }

    @Test
    @DisplayName("should set and get user")
    void session_SetAndGetUser() {
        Session session = new Session();
        User user = new User();
        user.setId(1L);
        user.setEmail("alice@example.com");

        session.setUser(user);

        assertThat(session.getUser()).isSameAs(user);
        assertThat(session.getUser().getEmail()).isEqualTo("alice@example.com");
    }

    @Test
    @DisplayName("should inherit id, createdAt, lastUpdatedAt, status from BaseModel")
    void session_InheritsBaseModelFields() {
        Session session = new Session();
        Date now = new Date();

        session.setId(3L);
        session.setCreatedAt(now);
        session.setLastUpdatedAt(now);
        session.setStatus(Status.ACTIVE);

        assertThat(session.getId()).isEqualTo(3L);
        assertThat(session.getCreatedAt()).isEqualTo(now);
        assertThat(session.getLastUpdatedAt()).isEqualTo(now);
        assertThat(session.getStatus()).isEqualTo(Status.ACTIVE);
    }

    @Test
    @DisplayName("should allow status to be set to INACTIVE")
    void session_StatusCanBeSetToInactive() {
        Session session = new Session();
        session.setStatus(Status.ACTIVE);
        assertThat(session.getStatus()).isEqualTo(Status.ACTIVE);

        session.setStatus(Status.INACTIVE);
        assertThat(session.getStatus()).isEqualTo(Status.INACTIVE);
    }
}
