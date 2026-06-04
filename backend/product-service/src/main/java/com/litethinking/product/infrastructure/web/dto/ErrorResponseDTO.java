package com.litethinking.product.infrastructure.web.dto;

import java.time.LocalDateTime;

public class ErrorResponseDTO {

    private String message;
    private LocalDateTime timestamp;
    private int status;

    public ErrorResponseDTO(String message, int status) {
        this.message = message;
        this.timestamp = LocalDateTime.now();
        this.status = status;
    }

    public String getMessage() { return message; }
    public void setMessage(String message) { this.message = message; }

    public LocalDateTime getTimestamp() { return timestamp; }
    public void setTimestamp(LocalDateTime timestamp) { this.timestamp = timestamp; }

    public int getStatus() { return status; }
    public void setStatus(int status) { this.status = status; }
}
