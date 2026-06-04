package com.litethinking.gateway.infrastructure.web.dto;

import lombok.Builder;
import lombok.Getter;

import java.time.LocalDateTime;

@Getter
@Builder
public class ErrorResponseDTO {

    private final String message;
    private final LocalDateTime timestamp;
    private final int status;
}
