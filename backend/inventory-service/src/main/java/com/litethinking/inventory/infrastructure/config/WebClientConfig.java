package com.litethinking.inventory.infrastructure.config;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.web.reactive.function.client.WebClient;

@Configuration
public class WebClientConfig {

    @Value("${services.company-url}")
    private String companyServiceUrl;

    @Bean
    public WebClient companyWebClient() {
        return WebClient.builder()
                .baseUrl(companyServiceUrl)
                .build();
    }
}
