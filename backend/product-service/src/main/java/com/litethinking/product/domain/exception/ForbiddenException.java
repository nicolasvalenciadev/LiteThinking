package com.litethinking.product.domain.exception;

public class ForbiddenException extends RuntimeException {
    public ForbiddenException() {
        super("No tiene permisos para realizar esta acción");
    }
}
