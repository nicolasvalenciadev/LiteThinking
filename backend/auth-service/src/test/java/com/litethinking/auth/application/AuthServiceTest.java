package com.litethinking.auth.application;

import com.litethinking.auth.application.service.AuthService;
import com.litethinking.auth.domain.exception.InvalidCredentialsException;
import com.litethinking.auth.domain.exception.PersistenceException;
import com.litethinking.auth.domain.exception.UserNotFoundException;
import com.litethinking.auth.domain.model.Role;
import com.litethinking.auth.domain.model.User;
import com.litethinking.auth.domain.port.out.TokenPort;
import com.litethinking.auth.domain.port.out.UserRepositoryPort;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.dao.DataAccessResourceFailureException;
import org.springframework.security.crypto.password.PasswordEncoder;

import java.util.Optional;
import java.util.UUID;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.junit.jupiter.api.Assertions.assertTrue;
import static org.mockito.Mockito.never;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
class AuthServiceTest {

    @Mock
    private UserRepositoryPort userRepositoryPort;

    @Mock
    private TokenPort tokenPort;

    @Mock
    private PasswordEncoder passwordEncoder;

    @InjectMocks
    private AuthService authService;

    private User testUser;
    private final String rawPassword = "secret";
    private final String encodedPassword = "$2a$10$hashedpassword";

    @BeforeEach
    void setUp() {
        Role role = new Role(UUID.randomUUID(), "ADMIN");
        testUser = new User(UUID.randomUUID(), "testuser", encodedPassword, role, null);
    }

    @Test
    void login_withValidCredentials_shouldReturnTokenAndCallGenerationOnce() {
        // given
        String expectedToken = "jwt.token.value";
        when(userRepositoryPort.findByUsername("testuser")).thenReturn(Optional.of(testUser));
        when(passwordEncoder.matches(rawPassword, encodedPassword)).thenReturn(true);
        when(tokenPort.generateToken(testUser)).thenReturn(expectedToken);

        // when
        String result = authService.login("testuser", rawPassword);

        // then
        assertEquals(expectedToken, result);
        verify(userRepositoryPort).findByUsername("testuser");
        verify(passwordEncoder).matches(rawPassword, encodedPassword);
        verify(tokenPort).generateToken(testUser);
    }

    @Test
    void login_withWrongPassword_shouldThrowInvalidCredentialsException() {
        // given
        when(userRepositoryPort.findByUsername("testuser")).thenReturn(Optional.of(testUser));
        when(passwordEncoder.matches("wrongpassword", encodedPassword)).thenReturn(false);

        // when / then
        assertThrows(InvalidCredentialsException.class,
                () -> authService.login("testuser", "wrongpassword"));
        verify(tokenPort, never()).generateToken(testUser);
    }

    @Test
    void login_withNonExistentUser_shouldThrowUserNotFoundException() {
        // given
        when(userRepositoryPort.findByUsername("ghost")).thenReturn(Optional.empty());

        // when / then
        assertThrows(UserNotFoundException.class,
                () -> authService.login("ghost", rawPassword));
        verify(passwordEncoder, never()).matches(rawPassword, encodedPassword);
        verify(tokenPort, never()).generateToken(testUser);
    }

    @Test
    void login_whenRepositoryFails_shouldThrowPersistenceException() {
        // given
        when(userRepositoryPort.findByUsername("testuser"))
                .thenThrow(new DataAccessResourceFailureException("DB connection failed"));

        // when / then
        assertThrows(PersistenceException.class,
                () -> authService.login("testuser", rawPassword));
        verify(tokenPort, never()).generateToken(testUser);
    }

    @Test
    void validateToken_withValidToken_shouldReturnTrue() {
        // given
        String token = "valid.jwt.token";
        when(tokenPort.isValid(token)).thenReturn(true);

        // when
        boolean result = authService.validateToken(token);

        // then
        assertTrue(result);
        verify(tokenPort).isValid(token);
    }

    @Test
    void validateToken_withInvalidToken_shouldReturnFalse() {
        // given
        String token = "invalid.token";
        when(tokenPort.isValid(token)).thenReturn(false);

        // when
        boolean result = authService.validateToken(token);

        // then
        assertFalse(result);
        verify(tokenPort).isValid(token);
    }
}
