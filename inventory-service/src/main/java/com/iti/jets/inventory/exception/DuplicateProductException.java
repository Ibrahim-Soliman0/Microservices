package com.iti.jets.inventory.exception;

public class DuplicateProductException extends RuntimeException {
    public DuplicateProductException(Long productId) {
        super("Product with ID " + productId + " already exists in inventory");
    }
}
