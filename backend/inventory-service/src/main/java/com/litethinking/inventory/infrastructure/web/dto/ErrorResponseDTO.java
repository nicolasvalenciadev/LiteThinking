package com.litethinking.inventory.infrastructure.web.dto;

import java.time.Instant;
import java.util.List;

public class ErrorResponseDTO {

    private int status;
    private String message;
    private Instant timestamp;
    private List<FieldErrorDTO> errors;

    public ErrorResponseDTO() {
        this.timestamp = Instant.now();
    }

    public ErrorResponseDTO(int status, String message) {
        this();
        this.status = status;
        this.message = message;
    }

    public int getStatus() { return status; }
    public void setStatus(int status) { this.status = status; }

    public String getMessage() { return message; }
    public void setMessage(String message) { this.message = message; }

    public Instant getTimestamp() { return timestamp; }
    public void setTimestamp(Instant timestamp) { this.timestamp = timestamp; }

    public List<FieldErrorDTO> getErrors() { return errors; }
    public void setErrors(List<FieldErrorDTO> errors) { this.errors = errors; }

    public static class FieldErrorDTO {
        private String field;
        private String message;

        public FieldErrorDTO(String field, String message) {
            this.field = field;
            this.message = message;
        }

        public String getField() { return field; }
        public String getMessage() { return message; }
    }
}
