//package com.iti.jets.inventory.service;
//
//
//import com.iti.jets.inventory.dto.OrderEvent;
//import lombok.RequiredArgsConstructor;
//import lombok.extern.slf4j.Slf4j;
//import org.springframework.amqp.rabbit.annotation.RabbitListener;
//import org.springframework.stereotype.Service;
//
//@Slf4j
//@Service
//@RequiredArgsConstructor
//public class OrderEventListener {
//
//    @RabbitListener(queues = "${rabbitmq.queue}")
//    public void handleOrderEvent(OrderEvent orderEvent) {
//        log.info("Received order event: orderId={}, status={}, userId={}, totalPrice={}",
//                orderEvent.getOrderId(),
//                orderEvent.getStatus(),
//                orderEvent.getUserId(),
//                orderEvent.getTotalPrice());
//
//        switch (orderEvent.getStatus()) {
//            case "CONFIRMED" -> handleConfirmedOrder(orderEvent);
//            case "FAILED"    -> handleFailedOrder(orderEvent);
//            case "CANCELLED" -> handleCancelledOrder(orderEvent);
//            default          -> log.warn("Unhandled order status: {}", orderEvent.getStatus());
//        }
//    }
//
//    private void handleConfirmedOrder(OrderEvent event) {
//        log.info("Order {} confirmed for user {}. Items: {}",
//                event.getOrderId(), event.getUserId(), event.getItems().size());
//        // TODO: trigger invoice generation, notifications, etc.
//    }
//
//    private void handleFailedOrder(OrderEvent event) {
//        log.warn("Order {} failed for user {}. Investigate if stock rollback is needed.",
//                event.getOrderId(), event.getUserId());
//        // TODO: compensating transaction logic if needed
//    }
//
//    private void handleCancelledOrder(OrderEvent event) {
//        log.info("Order {} was cancelled. Consider restoring stock if policy requires it.",
//                event.getOrderId());
//        // TODO: optional stock restoration logic
//    }
//}