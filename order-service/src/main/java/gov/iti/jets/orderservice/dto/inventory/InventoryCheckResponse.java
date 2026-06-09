package gov.iti.jets.orderservice.dto.inventory;

import lombok.Data;

@Data
public class InventoryCheckResponse {
    private Long productId;
    private String productName;
    private Double unitPrice;
    private boolean available;
    private String message;
}
