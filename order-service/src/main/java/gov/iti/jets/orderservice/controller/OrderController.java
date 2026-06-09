package gov.iti.jets.orderservice.controller;

import gov.iti.jets.orderservice.dto.order.CreateOrderRequest;
import gov.iti.jets.orderservice.dto.order.OrderResponse;
import gov.iti.jets.orderservice.service.OrderService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@Slf4j
@RestController
@RequestMapping("api/orders")
@RequiredArgsConstructor
public class OrderController {

    private final OrderService orderService;

    @PostMapping
    public ResponseEntity<OrderResponse> createOrder(@Valid @RequestBody CreateOrderRequest request){
        log.info("POST /api/orders - userId={}", request.getUserId());
        OrderResponse response = orderService.createOrder(request);
        return ResponseEntity.status(HttpStatus.CREATED).body(response);
    }

    @GetMapping("/{id}")
    public ResponseEntity<OrderResponse> getOrderById(@PathVariable Long id) {
        log.info("GET /api/orders/{}", id);
        return ResponseEntity.ok(orderService.getOrderById(id));
    }


    @GetMapping("/user/{userId}")
    public ResponseEntity<List<OrderResponse>> getOrdersByUser(@PathVariable Long userId) {
        log.info("GET /api/orders/user/{}", userId);
        return ResponseEntity.ok(orderService.getOrdersByUser(userId));
    }

    @GetMapping
    public ResponseEntity<List<OrderResponse>> getAllOrders() {
        log.info("GET /api/orders - all");
        return ResponseEntity.ok(orderService.getAllOrders());
    }
}
