package org.ecommerce.userauthenticationservice.configurations;

import io.jsonwebtoken.Jwts;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.boot.autoconfigure.http.client.HttpClientProperties;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;

import javax.crypto.SecretKey;

import static org.assertj.core.api.Assertions.assertThat;

@ExtendWith(MockitoExtension.class)
class SecurityConfigTest {

    @Mock
    private HttpClientProperties httpClientProperties;

    private SecurityConfig securityConfig;

    @BeforeEach
    void setUp() {
        securityConfig = new SecurityConfig(httpClientProperties);
    }

    // ========== BCryptPasswordEncoder bean ==========

    @Test
    @DisplayName("bCryptPasswordEncoder() should return a non-null BCryptPasswordEncoder")
    void bCryptPasswordEncoder_IsNotNull() {
        BCryptPasswordEncoder encoder = securityConfig.bCryptPasswordEncoder();
        assertThat(encoder).isNotNull().isInstanceOf(BCryptPasswordEncoder.class);
    }

    @Test
    @DisplayName("bCryptPasswordEncoder() should produce an encoded string different from the raw password")
    void bCryptPasswordEncoder_EncodesPassword() {
        BCryptPasswordEncoder encoder = securityConfig.bCryptPasswordEncoder();
        String raw = "mySecret";
        String encoded = encoder.encode(raw);
        assertThat(encoded).isNotEqualTo(raw);
    }

    @Test
    @DisplayName("bCryptPasswordEncoder() should verify that an encoded password matches the raw password")
    void bCryptPasswordEncoder_MatchesEncodedPassword() {
        BCryptPasswordEncoder encoder = securityConfig.bCryptPasswordEncoder();
        String raw = "mySecret";
        String encoded = encoder.encode(raw);
        assertThat(encoder.matches(raw, encoded)).isTrue();
    }

    @Test
    @DisplayName("bCryptPasswordEncoder() should reject a wrong password against an encoded one")
    void bCryptPasswordEncoder_DoesNotMatchWrongPassword() {
        BCryptPasswordEncoder encoder = securityConfig.bCryptPasswordEncoder();
        String encoded = encoder.encode("correctPassword");
        assertThat(encoder.matches("wrongPassword", encoded)).isFalse();
    }

    // ========== SecretKey bean ==========

    @Test
    @DisplayName("secretKey() should return a non-null SecretKey")
    void secretKey_IsNotNull() {
        SecretKey key = securityConfig.secretKey();
        assertThat(key).isNotNull();
    }

    @Test
    @DisplayName("secretKey() should return an HMAC-SHA256 key")
    void secretKey_IsHmacSha256() {
        SecretKey key = securityConfig.secretKey();
        assertThat(key.getAlgorithm()).isEqualTo("HmacSHA256");
    }

    @Test
    @DisplayName("secretKey() should produce a key that can sign and verify a JWT")
    void secretKey_CanSignAndVerifyJwt() {
        SecretKey key = securityConfig.secretKey();
        String token = Jwts.builder()
                .claim("sub", "test")
                .signWith(key)
                .compact();

        // parsing with the same key should not throw
        var claims = Jwts.parser().verifyWith(key).build()
                .parseSignedClaims(token)
                .getPayload();
        assertThat(claims.get("sub", String.class)).isEqualTo("test");
    }

    @Test
    @DisplayName("secretKey() should generate a different key on each call (random)")
    void secretKey_GeneratesDifferentKeyOnEachCall() {
        SecretKey key1 = securityConfig.secretKey();
        SecretKey key2 = securityConfig.secretKey();
        // Two independently generated HS256 keys should not share the same encoded bytes
        assertThat(key1.getEncoded()).isNotEqualTo(key2.getEncoded());
    }
}
