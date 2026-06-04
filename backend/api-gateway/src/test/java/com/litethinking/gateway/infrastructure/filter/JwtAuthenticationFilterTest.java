package com.litethinking.gateway.infrastructure.filter;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.datatype.jsr310.JavaTimeModule;
import com.litethinking.gateway.domain.port.in.TokenValidationUseCase;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.cloud.gateway.filter.GatewayFilterChain;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;
import org.springframework.mock.http.server.reactive.MockServerHttpRequest;
import org.springframework.mock.web.server.MockServerWebExchange;
import reactor.core.publisher.Mono;
import reactor.test.StepVerifier;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class JwtAuthenticationFilterTest {

    private static final String JWT_SECRET = "thisIsADefaultSecretKeyForDevelopmentOnly1234567890";

    @Mock
    private TokenValidationUseCase tokenValidationUseCase;

    @Mock
    private GatewayFilterChain filterChain;

    private JwtAuthenticationFilter filter;

    @BeforeEach
    void setUp() {
        ObjectMapper objectMapper = new ObjectMapper();
        objectMapper.registerModule(new JavaTimeModule());
        filter = new JwtAuthenticationFilter(tokenValidationUseCase, objectMapper, JWT_SECRET);
    }

    @Test
    void filter_returns401_whenNoAuthorizationHeader() {
        MockServerHttpRequest request = MockServerHttpRequest
                .get("/api/companies/1")
                .build();
        MockServerWebExchange exchange = MockServerWebExchange.from(request);

        StepVerifier.create(filter.filter(exchange, filterChain))
                .verifyComplete();

        assertThat(exchange.getResponse().getStatusCode()).isEqualTo(HttpStatus.UNAUTHORIZED);
        verify(filterChain, never()).filter(any());
    }

    @Test
    void filter_returns401_whenTokenIsInvalid() {
        when(tokenValidationUseCase.validateToken("invalid-token")).thenReturn(Mono.just(false));

        MockServerHttpRequest request = MockServerHttpRequest
                .get("/api/companies/1")
                .header(HttpHeaders.AUTHORIZATION, "Bearer invalid-token")
                .build();
        MockServerWebExchange exchange = MockServerWebExchange.from(request);

        StepVerifier.create(filter.filter(exchange, filterChain))
                .verifyComplete();

        assertThat(exchange.getResponse().getStatusCode()).isEqualTo(HttpStatus.UNAUTHORIZED);
        verify(filterChain, never()).filter(any());
    }

    @Test
    void filter_skipsValidation_forPublicRoute() {
        MockServerHttpRequest request = MockServerHttpRequest
                .post("/api/auth/login")
                .build();
        MockServerWebExchange exchange = MockServerWebExchange.from(request);

        when(filterChain.filter(any())).thenReturn(Mono.empty());

        StepVerifier.create(filter.filter(exchange, filterChain))
                .verifyComplete();

        verify(tokenValidationUseCase, never()).validateToken(any());
        verify(filterChain, times(1)).filter(any());
    }
}
