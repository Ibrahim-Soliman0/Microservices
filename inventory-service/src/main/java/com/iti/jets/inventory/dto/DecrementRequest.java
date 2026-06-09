package com.iti.jets.inventory.dto;

import jakarta.validation.Valid;
import jakarta.validation.constraints.NotEmpty;
import lombok.Data;

import java.util.List;

@Data
public class DecrementRequest {

    @NotEmpty(message = "Items list must not be empty")
    @Valid
    private List<InventoryCheckRequest> items;
}