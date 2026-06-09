package gov.iti.jets.orderservice.dto.order;

import lombok.Data;

import java.util.List;

@Data
public class OrderEvent {
    private Long orderId;
    private Long userId;
    private Double totalPrice;
    private String status;
    private List<OrderItemResponse> items;
}