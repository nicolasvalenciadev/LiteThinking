package com.litethinking.gateway.infrastructure.adapter;

import com.litethinking.gateway.domain.port.out.AuthServicePort;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;
import org.springframework.web.reactive.function.client.WebClient;
import reactor.core.publisher.Mono;

import java.time.Duration;

@Component
public class AuthServiceAdapter implements AuthServicePort {

    private final WebClient webClient;
    private final String authServiceUrl;

    public AuthServiceAdapter(WebClient webClient,
                              @Value("${services.auth-url}") String authServiceUrl) {
        this.webClient = webClient;
        this.authServiceUrl = authServiceUrl;
    }

    @Override
    public Mono<Boolean> validateWithAuthService(String token) {
        return webClient.get()
                .uri(authServiceUrl + "/api/auth/validate?token={token}", token)
                .retrieve()
                .bodyToMono(Boolean.class)
                .timeout(Duration.ofSeconds(5))
                .onErrorReturn(false);
    }
}
