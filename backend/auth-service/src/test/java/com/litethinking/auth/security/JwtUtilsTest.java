package com.litethinking.auth.security;

import com.litethinking.auth.domain.model.Role;
import com.litethinking.auth.domain.model.User;
import com.litethinking.auth.infrastructure.security.JwtUtils;
import io.jsonwebtoken.ExpiredJwtException;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import java.util.Base64;
import java.util.UUID;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.junit.jupiter.api.Assertions.assertTrue;

class JwtUtilsTest {

    private static final String BASE64_SECRET = Base64.getEncoder().encodeToString(
            "litethinking-auth-service-test-secret-key-256bits".getBytes()
    );
    private static final long EXPIRATION_MS = 86400000L;

    private JwtUtils jwtUtils;
    private User testUser;

    @BeforeEach
    void setUp() {
        jwtUtils = new JwtUtils(BASE64_SECRET, EXPIRATION_MS);
        Role role = new Role(UUID.randomUUID(), "ADMIN");
        testUser = new User(UUID.randomUUID(), "testuser", "hashedpassword", role, null);
    }

    @Test
    void buildToken_shouldReturnNonNullToken() {
        // given
        // testUser is provided by setUp

        // when
        String token = jwtUtils.buildToken(testUser);

        // then
        assertNotNull(token);
        assertFalse(token.isBlank());
    }

    @Test
    void buildToken_shouldEmbedUsernameAsSubject() {
        // given
        // testUser is provided by setUp

        // when
        String token = jwtUtils.buildToken(testUser);

        // then
        assertEquals(testUser.getUsername(), jwtUtils.extractUsername(token));
    }

    @Test
    void validateToken_withValidToken_shouldReturnTrue() {
        // given
        String token = jwtUtils.buildToken(testUser);

        // when
        boolean result = jwtUtils.validateToken(token);

        // then
        assertTrue(result);
    }

    @Test
    void validateToken_withExpiredToken_shouldReturnFalse() {
        // given
        JwtUtils expiredUtils = new JwtUtils(BASE64_SECRET, -1L);
        String expiredToken = expiredUtils.buildToken(testUser);

        // when
        boolean result = jwtUtils.validateToken(expiredToken);

        // then
        assertFalse(result);
    }

    @Test
    void validateToken_withMalformedToken_shouldReturnFalse() {
        // given
        String malformedToken = "this.is.not.a.valid.jwt";

        // when
        boolean result = jwtUtils.validateToken(malformedToken);

        // then
        assertFalse(result);
    }

    @Test
    void validateToken_withEmptyToken_shouldReturnFalse() {
        // given
        String emptyToken = "";

        // when
        boolean result = jwtUtils.validateToken(emptyToken);

        // then
        assertFalse(result);
    }

    @Test
    void extractClaims_withExpiredToken_shouldThrowExpiredJwtException() {
        // given
        JwtUtils expiredUtils = new JwtUtils(BASE64_SECRET, -1L);
        String expiredToken = expiredUtils.buildToken(testUser);

        // when / then
        assertThrows(ExpiredJwtException.class, () -> jwtUtils.extractClaims(expiredToken));
    }

    @Test
    void extractClaims_shouldContainRoleClaim() {
        // given
        String token = jwtUtils.buildToken(testUser);

        // when
        String role = jwtUtils.extractClaims(token).get("role", String.class);

        // then
        assertEquals(testUser.getRole().getName(), role);
    }

    @Test
    void extractClaims_shouldContainUserIdClaim() {
        // given
        String token = jwtUtils.buildToken(testUser);

        // when
        String userId = jwtUtils.extractClaims(token).get("userId", String.class);

        // then
        assertEquals(testUser.getId().toString(), userId);
    }
}
