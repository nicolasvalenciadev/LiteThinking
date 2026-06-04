package com.litethinking.auth.domain.exception;

public class InvalidCredentialsException extends RuntimeException {

    public InvalidCredentialsException() {
        super("Credenciales inválidas");
    }
}
