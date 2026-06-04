package com.litethinking.product.domain.exception;

import java.util.UUID;

public class CategoryNotFoundException extends RuntimeException {
    public CategoryNotFoundException(UUID id) {
        super("Categoría con id " + id + " no encontrada");
    }
}
