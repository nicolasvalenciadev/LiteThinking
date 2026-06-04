package com.litethinking.gateway.application.service;

import com.litethinking.gateway.domain.port.in.TokenValidationUseCase;
import com.litethinking.gateway.domain.port.out.AuthServicePort;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import reactor.core.publisher.Mono;

@Service
@RequiredArgsConstructor
public class TokenValidationService implements TokenValidationUseCase {

    private final AuthServicePort authServicePort;

    @Override
    public Mono<Boolean> validateToken(String token) {
        return authServicePort.validateWithAuthService(token)
                .onErrorReturn(false);
    }
}
