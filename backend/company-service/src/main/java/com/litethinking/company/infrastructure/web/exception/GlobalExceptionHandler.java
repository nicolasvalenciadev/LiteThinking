package com.litethinking.company.infrastructure.web.exception;

import com.litethinking.company.domain.exception.CompanyAlreadyExistsException;
import com.litethinking.company.domain.exception.CompanyNotFoundException;
import com.litethinking.company.infrastructure.web.dto.ErrorResponseDTO;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.validation.FieldError;
import org.springframework.web.bind.MethodArgumentNotValidException;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestControllerAdvice;

import java.time.LocalDateTime;
import java.util.stream.Collectors;

@RestControllerAdvice
public class GlobalExceptionHandler {

    @ExceptionHandler(CompanyNotFoundException.class)
    public ResponseEntity<ErrorResponseDTO> handleNotFound(CompanyNotFoundException ex) {
        return buildResponse(HttpStatus.NOT_FOUND, ex.getMessage());
    }

    @ExceptionHandler(CompanyAlreadyExistsException.class)
    public ResponseEntity<ErrorResponseDTO> handleAlreadyExists(CompanyAlreadyExistsException ex) {
        return buildResponse(HttpStatus.CONFLICT, ex.getMessage());
    }

    @ExceptionHandler(ForbiddenException.class)
    public ResponseEntity<ErrorResponseDTO> handleForbidden(ForbiddenException ex) {
        return buildResponse(HttpStatus.FORBIDDEN, ex.getMessage());
    }

    @ExceptionHandler(MethodArgumentNotValidException.class)
    public ResponseEntity<ErrorResponseDTO> handleValidation(MethodArgumentNotValidException ex) {
        String message = ex.getBindingResult().getFieldErrors().stream()
                .map(FieldError::getDefaultMessage)
                .collect(Collectors.joining(", "));
        return buildResponse(HttpStatus.BAD_REQUEST, message);
    }

    @ExceptionHandler(Exception.class)
    public ResponseEntity<ErrorResponseDTO> handleGeneric(Exception ex) {
        return buildResponse(HttpStatus.INTERNAL_SERVER_ERROR,
                "Ha ocurrido un error interno en el servidor");
    }

    private ResponseEntity<ErrorResponseDTO> buildResponse(HttpStatus status, String message) {
        ErrorResponseDTO error = ErrorResponseDTO.builder()
                .message(message)
                .timestamp(LocalDateTime.now())
                .status(status.value())
                .build();
        return ResponseEntity.status(status).body(error);
    }
}
