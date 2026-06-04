package com.litethinking.product.domain.exception;

import java.util.UUID;

public class ProductNotFoundException extends RuntimeException {
    public ProductNotFoundException(UUID id) {
        super("Producto con id " + id + " no encontrado");
    }
}
