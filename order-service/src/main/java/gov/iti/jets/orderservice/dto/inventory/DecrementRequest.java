package gov.iti.jets.orderservice.dto.inventory;

import lombok.Data;

import java.util.List;

@Data
public class DecrementRequest {
    private List<InventoryCheckRequest> items;
}