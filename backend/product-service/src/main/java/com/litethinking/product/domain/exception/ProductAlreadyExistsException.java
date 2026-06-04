package com.litethinking.product.domain.exception;

public class ProductAlreadyExistsException extends RuntimeException {
    public ProductAlreadyExistsException(String code) {
        super("Ya existe un producto con el código " + code);
    }
}
