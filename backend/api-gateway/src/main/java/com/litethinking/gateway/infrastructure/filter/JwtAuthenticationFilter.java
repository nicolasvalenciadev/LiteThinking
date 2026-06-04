package com.litethinking.gateway.infrastructure.filter;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.litethinking.gateway.domain.port.in.TokenValidationUseCase;
import com.litethinking.gateway.infrastructure.web.dto.ErrorResponseDTO;
import io.jsonwebtoken.Claims;
import io.jsonwebtoken.Jwts;
import io.jsonwebtoken.io.Decoders;
import io.jsonwebtoken.security.Keys;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.cloud.gateway.filter.GatewayFilter;
import org.springframework.cloud.gateway.filter.GatewayFilterChain;
import org.springframework.core.io.buffer.DataBuffer;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.server.reactive.ServerHttpRequest;
import org.springframework.http.server.reactive.ServerHttpResponse;
import org.springframework.stereotype.Component;
import org.springframework.web.server.ServerWebExchange;
import reactor.core.publisher.Mono;

import javax.crypto.SecretKey;
import java.time.LocalDateTime;
import java.util.List;

@Component
public class JwtAuthenticationFilter implements GatewayFilter {

    private static final List<String> PUBLIC_PATHS = List.of(
            "/api/auth/login",
            "/api/auth/validate"
    );

    private final TokenValidationUseCase tokenValidationUseCase;
    private final ObjectMapper objectMapper;
    private final SecretKey secretKey;

    public JwtAuthenticationFilter(TokenValidationUseCase tokenValidationUseCase,
                                   ObjectMapper objectMapper,
                                   @Value("${jwt.secret}") String jwtSecret) {
        this.tokenValidationUseCase = tokenValidationUseCase;
        this.objectMapper = objectMapper;
        this.secretKey = Keys.hmacShaKeyFor(Decoders.BASE64.decode(jwtSecret));
    }

    @Override
    public Mono<Void> filter(ServerWebExchange exchange, GatewayFilterChain chain) {
        ServerHttpRequest request = exchange.getRequest();

        if (isPublicPath(request)) {
            return chain.filter(exchange);
        }

        String authHeader = request.getHeaders().getFirst(HttpHeaders.AUTHORIZATION);

        if (authHeader == null || !authHeader.startsWith("Bearer ")) {
            return writeErrorResponse(exchange, "Token de autorización no proporcionado o con formato inválido");
        }

        String token = authHeader.substring(7);

        return tokenValidationUseCase.validateToken(token)
                .flatMap(isValid -> {
                    if (!isValid) {
                        return writeErrorResponse(exchange, "Token inválido o expirado");
                    }
                    try {
                        Claims claims = extractClaims(token);
                        String username = claims.getSubject();
                        String userId = String.valueOf(claims.get("userId"));
                        String role = String.valueOf(claims.get("role"));

                        ServerHttpRequest enrichedRequest = request.mutate()
                                .header("X-User-Id", userId)
                                .header("X-Username", username)
                                .header("X-User-Role", role)
                                .build();
                        return chain.filter(exchange.mutate().request(enrichedRequest).build());
                    } catch (Exception e) {
                        return writeErrorResponse(exchange, "No se pudieron extraer los datos del token");
                    }
                });
    }

    private boolean isPublicPath(ServerHttpRequest request) {
        return PUBLIC_PATHS.stream()
                .anyMatch(path -> request.getPath().value().startsWith(path));
    }

    private Claims extractClaims(String token) {
        return Jwts.parser()
                .verifyWith(secretKey)
                .build()
                .parseSignedClaims(token)
                .getPayload();
    }

    private Mono<Void> writeErrorResponse(ServerWebExchange exchange, String message) {
        ServerHttpResponse response = exchange.getResponse();
        response.setStatusCode(HttpStatus.UNAUTHORIZED);
        response.getHeaders().setContentType(MediaType.APPLICATION_JSON);

        ErrorResponseDTO errorBody = ErrorResponseDTO.builder()
                .message(message)
                .timestamp(LocalDateTime.now())
                .status(HttpStatus.UNAUTHORIZED.value())
                .build();

        try {
            byte[] bytes = objectMapper.writeValueAsBytes(errorBody);
            DataBuffer buffer = response.bufferFactory().wrap(bytes);
            return response.writeWith(Mono.just(buffer));
        } catch (JsonProcessingException e) {
            return response.setComplete();
        }
    }
}
