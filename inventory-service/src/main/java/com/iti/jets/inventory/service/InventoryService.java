package com.iti.jets.inventory.service;


import com.iti.jets.inventory.dto.*;
import com.iti.jets.inventory.exception.*;
import com.iti.jets.inventory.dto.InventoryCheckRequest;
import com.iti.jets.inventory.dto.InventoryCheckResponse;
import com.iti.jets.inventory.model.InventoryItem;
import com.iti.jets.inventory.repository.InventoryItemRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

@Slf4j
@Service
@RequiredArgsConstructor
public class InventoryService {

    private final InventoryItemRepository inventoryItemRepository;

    @Transactional(readOnly = true)
    public InventoryCheckResponse checkAvailability(Long productId, Integer quantity) {
        return inventoryItemRepository.findByProductId(productId)
                .map(item -> {
                    boolean available = item.getQuantity() >= quantity;
                    return InventoryCheckResponse.builder()
                            .productId(Long.valueOf(item.getProductId()))
                            .productName(item.getProductName())
                            .unitPrice(item.getUnitPrice())
                            .available(available)
                            .message(available
                                    ? "In stock"
                                    : "Only " + item.getQuantity() + " unit(s) available")
                            .build();
                })
                .orElseGet(() -> InventoryCheckResponse.builder()
                        .productId(productId)
                        .available(false)
                        .message("Product not found in inventory")
                        .build());
    }

    @Transactional
    public void decrementStock(DecrementRequest decrementRequest) {
        List<InventoryCheckRequest> items = decrementRequest.getItems();

        for (InventoryCheckRequest item : items) {
            InventoryItem inventoryItem = inventoryItemRepository
                    .findByProductId(item.getProductId())
                    .orElseThrow(() -> new InventoryItemNotFoundException(item.getProductId()));

            if (inventoryItem.getQuantity() < item.getQuantity()) {
                throw new InsufficientStockException(
                        item.getProductId(),
                        item.getQuantity(),
                        inventoryItem.getQuantity()
                );
            }
        }

        for (InventoryCheckRequest item : items) {
            InventoryItem inventoryItem = inventoryItemRepository
                    .findByProductId(item.getProductId())
                    .orElseThrow(() -> new InventoryItemNotFoundException(item.getProductId()));

            inventoryItem.setQuantity(inventoryItem.getQuantity() - item.getQuantity());
            inventoryItemRepository.save(inventoryItem);
            log.info("Decremented stock for product {}: -{} units (remaining: {})",
                    item.getProductId(), item.getQuantity(), inventoryItem.getQuantity());
        }
    }

    @Transactional
    public InventoryItemResponse createItem(CreateInventoryItemRequest request) {
        if (inventoryItemRepository.findByProductId(request.getProductId()).isPresent()) {
            throw new DuplicateProductException(request.getProductId());
        }

        InventoryItem item = InventoryItem.builder()
                .productId(request.getProductId().intValue())
                .productName(request.getProductName())
                .quantity(request.getQuantity())
                .unitPrice(request.getUnitPrice())
                .build();

        InventoryItem saved = inventoryItemRepository.save(item);
        log.info("Created inventory item for product {}", saved.getProductId());
        return toResponse(saved);
    }

    @Transactional(readOnly = true)
    public List<InventoryItemResponse> getAllItems() {
        return inventoryItemRepository.findAll()
                .stream()
                .map(this::toResponse)
                .toList();
    }

    @Transactional(readOnly = true)
    public InventoryItemResponse getItemByProductId(Long productId) {
        InventoryItem item = inventoryItemRepository.findByProductId(productId)
                .orElseThrow(() -> new InventoryItemNotFoundException(productId));
        return toResponse(item);
    }

    @Transactional
    public InventoryItemResponse updateStock(Long productId, Integer quantity) {
        InventoryItem item = inventoryItemRepository.findByProductId(productId)
                .orElseThrow(() -> new InventoryItemNotFoundException(productId));
        item.setQuantity(quantity);
        InventoryItem saved = inventoryItemRepository.save(item);
        log.info("Updated stock for product {} to {}", productId, quantity);
        return toResponse(saved);
    }

    @Transactional
    public void deleteItem(Long productId) {
        InventoryItem item = inventoryItemRepository.findByProductId(productId)
                .orElseThrow(() -> new InventoryItemNotFoundException(productId));
        inventoryItemRepository.delete(item);
        log.info("Deleted inventory item for product {}", productId);
    }

    private InventoryItemResponse toResponse(InventoryItem item) {
        return InventoryItemResponse.builder()
                .id(Long.valueOf(item.getId()))
                .productId(Long.valueOf(item.getProductId()))
                .productName(item.getProductName())
                .quantity(item.getQuantity())
                .unitPrice(item.getUnitPrice())
                .build();
    }
}