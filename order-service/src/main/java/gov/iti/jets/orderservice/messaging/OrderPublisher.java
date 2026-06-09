package gov.iti.jets.orderservice.messaging;

import gov.iti.jets.orderservice.dto.order.OrderEvent;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.amqp.rabbit.core.RabbitTemplate;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;

@Slf4j
@Component
@RequiredArgsConstructor
public class OrderPublisher {

    private final RabbitTemplate rabbitTemplate;

    @Value("${rabbitmq.exchange}")
    private String exchange;

    @Value("${rabbitmq.routing-key}")
    private String invoiceRoutingKey;

    public void publishOrderEvent(OrderEvent event) {
        log.info("Publishing order event to RabbitMQ for orderId={}", event.getOrderId());
        rabbitTemplate.convertAndSend(exchange, invoiceRoutingKey, event);
        log.info("Order event published successfully for orderId={}", event.getOrderId());
    }
}
