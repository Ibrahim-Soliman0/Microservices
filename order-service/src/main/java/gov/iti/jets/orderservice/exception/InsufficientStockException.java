package gov.iti.jets.orderservice.exception;

public class InsufficientStockException extends RuntimeException {
    public InsufficientStockException(Long productId) {
        super("Insufficient stock for product with id: " + productId);
    }
}
