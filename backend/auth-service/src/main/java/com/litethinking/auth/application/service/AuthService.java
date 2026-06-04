package com.litethinking.auth.application.service;

import com.litethinking.auth.domain.exception.InvalidCredentialsException;
import com.litethinking.auth.domain.exception.PersistenceException;
import com.litethinking.auth.domain.exception.UserNotFoundException;
import com.litethinking.auth.domain.model.User;
import com.litethinking.auth.domain.port.in.AuthUseCase;
import com.litethinking.auth.domain.port.out.TokenPort;
import com.litethinking.auth.domain.port.out.UserRepositoryPort;
import org.springframework.dao.DataAccessException;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
public class AuthService implements AuthUseCase {

    private final UserRepositoryPort userRepositoryPort;
    private final TokenPort tokenPort;
    private final PasswordEncoder passwordEncoder;

    public AuthService(UserRepositoryPort userRepositoryPort,
                       TokenPort tokenPort,
                       PasswordEncoder passwordEncoder) {
        this.userRepositoryPort = userRepositoryPort;
        this.tokenPort = tokenPort;
        this.passwordEncoder = passwordEncoder;
    }

    @Override
    @Transactional(readOnly = true)
    public String login(String username, String password) {
        User user = findUserOrThrow(username);
        verifyPassword(password, user);
        return tokenPort.generateToken(user);
    }

    @Override
    public boolean validateToken(String token) {
        return tokenPort.isValid(token);
    }

    private User findUserOrThrow(String username) {
        try {
            return userRepositoryPort.findByUsername(username)
                    .orElseThrow(() -> new UserNotFoundException(username));
        } catch (DataAccessException ex) {
            throw new PersistenceException("Error al acceder al repositorio de usuarios", ex);
        }
    }

    private void verifyPassword(String rawPassword, User user) {
        if (!passwordEncoder.matches(rawPassword, user.getPassword())) {
            throw new InvalidCredentialsException();
        }
    }
}
