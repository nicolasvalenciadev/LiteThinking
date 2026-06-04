package com.litethinking.inventory.domain.exception;

public class InventoryQueryException extends RuntimeException {

    public InventoryQueryException(String message) {
        super(message);
    }

    public InventoryQueryException(String message, Throwable cause) {
        super(message, cause);
    }
}
