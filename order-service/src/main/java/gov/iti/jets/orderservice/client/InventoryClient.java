package gov.iti.jets.orderservice.client;


import gov.iti.jets.orderservice.dto.inventory.DecrementRequest;
import gov.iti.jets.orderservice.dto.inventory.InventoryCheckResponse;
import gov.iti.jets.orderservice.exception.InventoryServiceException;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Component;
import org.springframework.web.client.HttpClientErrorException;
import org.springframework.web.client.RestTemplate;

@Slf4j
@Component
@RequiredArgsConstructor
public class InventoryClient {

    private final RestTemplate restTemplate;

    @Value("${inventory.service.url}")
    private String inventoryServiceUrl;

    public InventoryCheckResponse checkAvailability(Long productId, Integer quantity) {
        String url = inventoryServiceUrl + "/api/inventory/check/" + productId + "?quantity=" + quantity;
        log.debug("Checking inventory for productId={} qty={}", productId, quantity);
        try {
            ResponseEntity<InventoryCheckResponse> response =
                    restTemplate.getForEntity(url, InventoryCheckResponse.class);
            return response.getBody();
        } catch (HttpClientErrorException e) {
            log.error("Inventory check failed for productId={}: {}", productId, e.getMessage());
            throw new InventoryServiceException("Inventory service error for product " + productId + ": " + e.getMessage());
        } catch (Exception e) {
            log.error("Could not reach inventory-service: {}", e.getMessage());
            throw new InventoryServiceException("Inventory service is unavailable. Please try again later.");
        }
    }

    public void decrementStock(DecrementRequest request) {
        String url = inventoryServiceUrl + "/api/inventory/decrement";
        log.debug("Decrementing stock for {} items", request.getItems().size());
        try {
            restTemplate.put(url, request);
        } catch (HttpClientErrorException e) {
            log.error("Stock decrement failed: {}", e.getMessage());
            throw new InventoryServiceException("Failed to decrement stock: " + e.getMessage());
        } catch (Exception e) {
            log.error("Could not reach inventory-service for decrement: {}", e.getMessage());
            throw new InventoryServiceException("Inventory service is unavailable. Please try again later.");
        }
    }
}
