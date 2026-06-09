package com.iti.jets.inventory.exception;

public class InventoryItemNotFoundException extends RuntimeException {
    public InventoryItemNotFoundException(Long productId) {
        super("Inventory item not found for product ID: " + productId);
    }
}
