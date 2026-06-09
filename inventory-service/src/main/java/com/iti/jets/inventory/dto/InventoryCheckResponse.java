package com.iti.jets.inventory.dto;

import lombok.Builder;
import lombok.Data;

@Data
@Builder
public class InventoryCheckResponse {

    private Long productId;
    private String productName;
    private Double unitPrice;
    private boolean available;
    private String message;
}