package com.litethinking.gateway.application.service;

import com.litethinking.gateway.domain.port.out.AuthServicePort;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import reactor.core.publisher.Mono;
import reactor.test.StepVerifier;

import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
class TokenValidationServiceTest {

    @Mock
    private AuthServicePort authServicePort;

    private TokenValidationService tokenValidationService;

    @BeforeEach
    void setUp() {
        tokenValidationService = new TokenValidationService(authServicePort);
    }

    @Test
    void validateToken_returnsTrue_whenAuthServiceReturnsTrue() {
        when(authServicePort.validateWithAuthService("valid-token")).thenReturn(Mono.just(true));

        StepVerifier.create(tokenValidationService.validateToken("valid-token"))
                .expectNext(true)
                .verifyComplete();
    }

    @Test
    void validateToken_returnsFalse_whenAuthServiceReturnsFalse() {
        when(authServicePort.validateWithAuthService("invalid-token")).thenReturn(Mono.just(false));

        StepVerifier.create(tokenValidationService.validateToken("invalid-token"))
                .expectNext(false)
                .verifyComplete();
    }

    @Test
    void validateToken_returnsFalse_whenAuthServiceThrowsException() {
        when(authServicePort.validateWithAuthService("error-token"))
                .thenReturn(Mono.error(new RuntimeException("Servicio no disponible")));

        StepVerifier.create(tokenValidationService.validateToken("error-token"))
                .expectNext(false)
                .verifyComplete();
    }
}
