package com.iti.jets.inventory.dto;

import lombok.Builder;
import lombok.Data;

@Data
@Builder
public class InventoryItemResponse {
    private Long id;
    private Long productId;
    private String productName;
    private Integer quantity;
    private Double unitPrice;
}