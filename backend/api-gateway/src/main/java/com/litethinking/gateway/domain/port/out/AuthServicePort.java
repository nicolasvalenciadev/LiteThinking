package com.litethinking.gateway.domain.port.out;

import reactor.core.publisher.Mono;

public interface AuthServicePort {

    Mono<Boolean> validateWithAuthService(String token);
}