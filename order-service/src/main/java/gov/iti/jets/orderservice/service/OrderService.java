package gov.iti.jets.orderservice.service;


import gov.iti.jets.orderservice.client.InventoryClient;
import gov.iti.jets.orderservice.dto.inventory.DecrementRequest;
import gov.iti.jets.orderservice.dto.inventory.InventoryCheckRequest;
import gov.iti.jets.orderservice.dto.inventory.InventoryCheckResponse;
import gov.iti.jets.orderservice.dto.order.*;
import gov.iti.jets.orderservice.exception.InsufficientStockException;
import gov.iti.jets.orderservice.exception.OrderNotFoundException;
import gov.iti.jets.orderservice.messaging.OrderPublisher;
import gov.iti.jets.orderservice.model.Order;
import gov.iti.jets.orderservice.model.OrderItem;
import gov.iti.jets.orderservice.model.OrderStatus;
import gov.iti.jets.orderservice.repository.OrderRepository;
import jakarta.transaction.Transactional;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;

@Slf4j
@Service
@RequiredArgsConstructor
public class OrderService {

    private final OrderRepository orderRepository;
    private final InventoryClient inventoryClient;
    private final OrderPublisher orderPublisher;

    @Transactional
    public OrderResponse createOrder(CreateOrderRequest request){
        List<OrderItem> orderItems  = new ArrayList<>();
        double totalPrice = 0.0;

        for (OrderItemRequest item : request.getItems()){
            InventoryCheckResponse inventoryCheckResponse =
                    inventoryClient.checkAvailability(item.getProductId(),item.getQuantity());
            if(!inventoryCheckResponse.isAvailable()){
                log.warn("Product {} is out of stock", item.getProductId());
                throw new InsufficientStockException(item.getProductId());
            }

            OrderItem availableItem = OrderItem.builder()
                    .productId(inventoryCheckResponse.getProductId())
                    .productName(inventoryCheckResponse.getProductName())
                    .unitPrice(inventoryCheckResponse.getUnitPrice())
                    .quantity(item.getQuantity())
                    .build();
            orderItems.add(availableItem);
            totalPrice += availableItem.getUnitPrice() * availableItem.getQuantity();
        }

        Order order = Order.builder()
                .userId(request.getUserId())
                .status(OrderStatus.CONFIRMED)
                .totalPrice(totalPrice)
                .build();

        for(OrderItem item : orderItems){
            item.setOrder(order);
        }
        order.setItems(orderItems);

        Order savedOrder = orderRepository.save(order);
        log.info("Order saved with id={}", savedOrder.getId());

        List<InventoryCheckRequest> decrementItems = request.getItems().stream()
                .map(i -> {
                    var req = new InventoryCheckRequest();
                    req.setProductId(i.getProductId());
                    req.setQuantity(i.getQuantity());
                    return req;
                }).toList();
        DecrementRequest decrementRequest = new DecrementRequest();
        decrementRequest.setItems(decrementItems);
        inventoryClient.decrementStock(decrementRequest);
        log.info("Stock decremented for orderId={}", savedOrder.getId());

        OrderResponse response = mapToResponse(savedOrder);

        OrderEvent event = buildOrderEvent(savedOrder, response.getItems());
        orderPublisher.publishOrderEvent(event);

        return response;

    }
    public OrderResponse getOrderById(Long id) {
        log.debug("Fetching order id={}", id);
        Order order = orderRepository.findById(id)
                .orElseThrow(() -> new OrderNotFoundException(id));
        return mapToResponse(order);
    }

    public List<OrderResponse> getOrdersByUser(Long userId) {
        log.debug("Fetching orders for userId={}", userId);
        return orderRepository.findByUserId(userId)
                .stream()
                .map(this::mapToResponse)
                .toList();
    }

    public List<OrderResponse> getAllOrders() {
        return orderRepository.findAll()
                .stream()
                .map(this::mapToResponse)
                .toList();
    }

    private OrderResponse mapToResponse(Order order) {
        OrderResponse response = new OrderResponse();
        response.setId(order.getId());
        response.setUserId(order.getUserId());
        response.setStatus(order.getStatus().name());
        response.setTotalPrice(order.getTotalPrice());

        List<OrderItemResponse> itemResponses = order.getItems().stream()
                .map(item -> {
                    OrderItemResponse ir = new OrderItemResponse();
                    ir.setId(item.getId());
                    ir.setProductId(item.getProductId());
                    ir.setProductName(item.getProductName());
                    ir.setQuantity(item.getQuantity());
                    ir.setUnitPrice(item.getUnitPrice());
                    ir.setSubtotal(item.getUnitPrice() * item.getQuantity());
                    return ir;
                }).toList();

        response.setItems(itemResponses);
        return response;
    }

    private OrderEvent buildOrderEvent(Order order, List<OrderItemResponse> items) {
        OrderEvent event = new OrderEvent();
        event.setOrderId(order.getId());
        event.setUserId(order.getUserId());
        event.setTotalPrice(order.getTotalPrice());
        event.setStatus(order.getStatus().name());
        event.setItems(items);
        return event;
    }
}




