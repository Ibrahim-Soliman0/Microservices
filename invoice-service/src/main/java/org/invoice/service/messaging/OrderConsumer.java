package org.invoice.service.messaging;

import org.invoice.service.dto.OrderEvent;
import org.invoice.service.service.InvoiceService;
import org.springframework.amqp.rabbit.annotation.RabbitListener;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;

@Service
public class OrderConsumer {

    @Value("${rabbitmq.queue}")
    private String queueName;

    private final InvoiceService invoiceService;

    public OrderConsumer(InvoiceService invoiceService) {
        this.invoiceService = invoiceService;
    }

    @RabbitListener(queues = "invoice.queue")
    public void receiveOrderEvent(OrderEvent orderEvent) {
        invoiceService.createInvoice(orderEvent);
        System.out.println("Invoice Created!");
    }
}
