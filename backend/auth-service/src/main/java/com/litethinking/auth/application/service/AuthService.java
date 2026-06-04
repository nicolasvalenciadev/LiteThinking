package com.litethinking.auth.application.service;

import com.litethinking.auth.domain.exception.InvalidCredentialsException;
import com.litethinking.auth.domain.exception.UserNotFoundException;
import com.litethinking.auth.domain.model.User;
import com.litethinking.auth.domain.port.in.AuthUseCase;
import com.litethinking.auth.domain.port.out.TokenPort;
import com.litethinking.auth.domain.port.out.UserRepositoryPort;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

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
    public String login(String username, String password) {
        User user = userRepositoryPort.findByUsername(username)
                .orElseThrow(() -> new UserNotFoundException(username));

        if (!passwordEncoder.matches(password, user.getPassword())) {
            throw new InvalidCredentialsException();
        }

        return tokenPort.generateToken(user);
    }

    @Override
    public boolean validateToken(String token) {
        return tokenPort.isValid(token);
    }
}
