package com.litethinking.auth.domain.exception;

public class UserNotFoundException extends RuntimeException {

    public UserNotFoundException(String username) {
        super("Usuario no encontrado: " + username);
    }
}
