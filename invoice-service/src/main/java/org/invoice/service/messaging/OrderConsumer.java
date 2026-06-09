package org.invoice.service.messaging;

import lombok.AllArgsConstructor;
import org.invoice.service.dto.OrderEvent;
import org.invoice.service.entity.Invoice;
import org.invoice.service.service.InvoiceService;
import org.springframework.amqp.rabbit.annotation.RabbitListener;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;

@Service
public class OrderConsumer {

    @Value("${rabbitmq.queue}")
    private String queue;

    @Value("${rabbitmq.routing-key}")
    private String routingKey;

    private final InvoiceService invoiceService;

    public OrderConsumer(InvoiceService invoiceService) {
        this.invoiceService = invoiceService;
    }

    @RabbitListener(queues = "queue")
    public void receiveOrderEvent(OrderEvent orderEvent) {
        invoiceService.createInvoice(orderEvent);
    }
}
