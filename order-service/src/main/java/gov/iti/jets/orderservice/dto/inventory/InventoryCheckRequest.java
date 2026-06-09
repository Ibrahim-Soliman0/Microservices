package gov.iti.jets.orderservice.dto.inventory;

import lombok.Data;

@Data
public class InventoryCheckRequest {
    private Long productId;
    private Integer quantity;
}