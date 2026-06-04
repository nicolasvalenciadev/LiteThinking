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

    private JwtUtils jwtUtils;
    private User testUser;

    @BeforeEach
    void setUp() {
        jwtUtils = new JwtUtils(BASE64_SECRET, 86400000L);
        Role role = new Role(UUID.randomUUID(), "ADMIN");
        testUser = new User(UUID.randomUUID(), "testuser", "hashedpassword", role, null);
    }

    @Test
    void buildToken_shouldReturnNonNullToken() {
        String token = jwtUtils.buildToken(testUser);
        assertNotNull(token);
        assertFalse(token.isBlank());
    }

    @Test
    void buildToken_shouldEmbedUsernameInToken() {
        String token = jwtUtils.buildToken(testUser);
        String extractedUsername = jwtUtils.extractUsername(token);
        assertEquals(testUser.getUsername(), extractedUsername);
    }

    @Test
    void validateToken_withValidToken_shouldReturnTrue() {
        String token = jwtUtils.buildToken(testUser);
        assertTrue(jwtUtils.validateToken(token));
    }

    @Test
    void validateToken_withExpiredToken_shouldReturnFalse() {
        JwtUtils shortLivedUtils = new JwtUtils(BASE64_SECRET, -1L);
        String expiredToken = shortLivedUtils.buildToken(testUser);
        assertFalse(jwtUtils.validateToken(expiredToken));
    }

    @Test
    void validateToken_withMalformedToken_shouldReturnFalse() {
        assertFalse(jwtUtils.validateToken("this.is.not.a.valid.jwt"));
    }

    @Test
    void validateToken_withEmptyToken_shouldReturnFalse() {
        assertFalse(jwtUtils.validateToken(""));
    }

    @Test
    void extractClaims_withExpiredToken_shouldThrowExpiredJwtException() {
        JwtUtils shortLivedUtils = new JwtUtils(BASE64_SECRET, -1L);
        String expiredToken = shortLivedUtils.buildToken(testUser);
        assertThrows(ExpiredJwtException.class, () -> jwtUtils.extractClaims(expiredToken));
    }

    @Test
    void extractClaims_shouldContainRoleClaim() {
        String token = jwtUtils.buildToken(testUser);
        String role = jwtUtils.extractClaims(token).get("role", String.class);
        assertEquals(testUser.getRole().getName(), role);
    }

    @Test
    void extractClaims_shouldContainUserIdClaim() {
        String token = jwtUtils.buildToken(testUser);
        String userId = jwtUtils.extractClaims(token).get("userId", String.class);
        assertEquals(testUser.getId().toString(), userId);
    }
}
