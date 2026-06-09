package com.iti.jets.inventory.controller;


import com.iti.jets.inventory.dto.*;
import com.iti.jets.inventory.service.InventoryService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@Slf4j
@RestController
@RequestMapping("/api/inventory")
@RequiredArgsConstructor
public class InventoryController {

    private final InventoryService inventoryService;

    @GetMapping("/check/{productId}")
    public ResponseEntity<InventoryCheckResponse> checkAvailability(
            @PathVariable Long productId,
            @RequestParam Integer quantity) {
        log.info("Stock check requested for productId={} qty={}", productId, quantity);
        return ResponseEntity.ok(inventoryService.checkAvailability(productId, quantity));
    }
    @PutMapping("/decrement")
    public ResponseEntity<Void> decrementStock(
            @Valid @RequestBody DecrementRequest decrementRequest) {
        log.info("Decrement stock requested for {} item(s)", decrementRequest.getItems().size());
        inventoryService.decrementStock(decrementRequest);
        return ResponseEntity.noContent().build();
    }

    @PostMapping
    public ResponseEntity<InventoryItemResponse> createItem(
            @Valid @RequestBody CreateInventoryItemRequest request) {
        return ResponseEntity.status(HttpStatus.CREATED)
                .body(inventoryService.createItem(request));
    }

    @GetMapping
    public ResponseEntity<List<InventoryItemResponse>> getAllItems() {
        return ResponseEntity.ok(inventoryService.getAllItems());
    }

    @GetMapping("/{productId}")
    public ResponseEntity<InventoryItemResponse> getItemByProductId(
            @PathVariable Long productId) {
        return ResponseEntity.ok(inventoryService.getItemByProductId(productId));
    }

    @DeleteMapping("/{productId}")
    public ResponseEntity<Void> deleteItem(@PathVariable Long productId) {
        inventoryService.deleteItem(productId);
        return ResponseEntity.noContent().build();
    }
}