package com.litethinking.gateway.domain.port.in;

import reactor.core.publisher.Mono;

public interface TokenValidationUseCase {

    Mono<Boolean> validateToken(String token);
}