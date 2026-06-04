package com.litethinking.gateway.domain.model;

import lombok.AllArgsConstructor;
import lombok.Getter;

@Getter
@AllArgsConstructor
public class AuthenticatedRequest {

    private final String userId;
    private final String username;
    private final String role;
    private final String token;
}