package gov.iti.jets.orderservice.dto.order;

import lombok.Data;

import java.util.List;

@Data
public class OrderResponse {
    private Long id;
    private Long userId;
    private String status;
    private Double totalPrice;
    private List<OrderItemResponse> items;
}