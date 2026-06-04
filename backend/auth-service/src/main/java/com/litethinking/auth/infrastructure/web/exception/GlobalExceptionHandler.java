package com.litethinking.auth.infrastructure.web.exception;

import com.litethinking.auth.domain.exception.InvalidCredentialsException;
import com.litethinking.auth.domain.exception.UserNotFoundException;
import com.litethinking.auth.infrastructure.web.dto.ErrorResponseDTO;
import io.jsonwebtoken.ExpiredJwtException;
import io.jsonwebtoken.MalformedJwtException;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestControllerAdvice;

@RestControllerAdvice
public class GlobalExceptionHandler {

    @ExceptionHandler(InvalidCredentialsException.class)
    public ResponseEntity<ErrorResponseDTO> handleInvalidCredentials(InvalidCredentialsException ex) {
        return ResponseEntity
                .status(HttpStatus.UNAUTHORIZED)
                .body(new ErrorResponseDTO(HttpStatus.UNAUTHORIZED.value(), ex.getMessage()));
    }

    @ExceptionHandler(UserNotFoundException.class)
    public ResponseEntity<ErrorResponseDTO> handleUserNotFound(UserNotFoundException ex) {
        return ResponseEntity
                .status(HttpStatus.NOT_FOUND)
                .body(new ErrorResponseDTO(HttpStatus.NOT_FOUND.value(), ex.getMessage()));
    }

    @ExceptionHandler(ExpiredJwtException.class)
    public ResponseEntity<ErrorResponseDTO> handleExpiredJwt(ExpiredJwtException ex) {
        return ResponseEntity
                .status(HttpStatus.UNAUTHORIZED)
                .body(new ErrorResponseDTO(HttpStatus.UNAUTHORIZED.value(),
                        "Token expirado. Por favor, inicie sesión nuevamente."));
    }

    @ExceptionHandler(MalformedJwtException.class)
    public ResponseEntity<ErrorResponseDTO> handleMalformedJwt(MalformedJwtException ex) {
        return ResponseEntity
                .status(HttpStatus.BAD_REQUEST)
                .body(new ErrorResponseDTO(HttpStatus.BAD_REQUEST.value(),
                        "Token JWT malformado."));
    }

    @ExceptionHandler(Exception.class)
    public ResponseEntity<ErrorResponseDTO> handleGenericException(Exception ex) {
        return ResponseEntity
                .status(HttpStatus.INTERNAL_SERVER_ERROR)
                .body(new ErrorResponseDTO(HttpStatus.INTERNAL_SERVER_ERROR.value(),
                        "Error interno del servidor."));
    }
}
