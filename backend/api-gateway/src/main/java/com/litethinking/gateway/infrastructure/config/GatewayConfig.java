package com.litethinking.gateway.infrastructure.config;

import com.litethinking.gateway.infrastructure.filter.JwtAuthenticationFilter;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.cloud.gateway.route.RouteLocator;
import org.springframework.cloud.gateway.route.builder.RouteLocatorBuilder;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

@Configuration
public class GatewayConfig {

    private final JwtAuthenticationFilter jwtFilter;

    @Value("${services.auth-url}")
    private String authServiceUrl;

    @Value("${services.company-url}")
    private String companyServiceUrl;

    @Value("${services.product-url}")
    private String productServiceUrl;

    @Value("${services.inventory-url}")
    private String inventoryServiceUrl;

    public GatewayConfig(JwtAuthenticationFilter jwtFilter) {
        this.jwtFilter = jwtFilter;
    }

    @Bean
    public RouteLocator routeLocator(RouteLocatorBuilder builder) {
        return builder.routes()
                .route("auth-service", r -> r
                        .path("/api/auth/**")
                        .uri(authServiceUrl))
                .route("company-service", r -> r
                        .path("/api/companies/**")
                        .filters(f -> f.filter(jwtFilter))
                        .uri(companyServiceUrl))
                .route("product-service", r -> r
                        .path("/api/products/**")
                        .filters(f -> f.filter(jwtFilter))
                        .uri(productServiceUrl))
                .route("inventory-service", r -> r
                        .path("/api/inventory/**")
                        .filters(f -> f.filter(jwtFilter))
                        .uri(inventoryServiceUrl))
                .build();
    }
}
