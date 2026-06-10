package org.invoice.service.service;

import jakarta.persistence.EntityNotFoundException;
import lombok.AllArgsConstructor;
import org.invoice.service.dto.OrderEvent;
import org.invoice.service.entity.Invoice;
import org.invoice.service.entity.InvoiceItem;
import org.invoice.service.exception.InvoiceNotFoundException;
import org.invoice.service.repository.InvoiceItemRepository;
import org.invoice.service.repository.InvoiceRepository;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
@AllArgsConstructor
public class InvoiceService {

    private InvoiceRepository invoiceRepository;

    private InvoiceItemRepository invoiceItemRepository;

    public List<Invoice> getAllInvoicesByUserId(Long userId) {

        return invoiceRepository.getAllByUserId(userId);
    }

    public Invoice getInvoiceById(Long invoiceId) {

        Invoice invoice;

        try {
            invoice = invoiceRepository.getReferenceById(invoiceId);
        } catch (EntityNotFoundException entityNotFoundException) {
            throw new InvoiceNotFoundException("Invoice with id[%s] not found".formatted(invoiceId));
        }

        return invoice;
    }

    public Long createInvoice(OrderEvent orderEvent) {

        Invoice toAddInvoice = new Invoice();
        toAddInvoice.setOrderId(orderEvent.getOrderId());
        toAddInvoice.setUserId(orderEvent.getUserId());
        toAddInvoice.setTotalPrice(orderEvent.getTotalPrice());

        Invoice invoice = invoiceRepository.saveAndFlush(toAddInvoice);

        List<InvoiceItem> invoiceItems = orderEvent.getItems()
                .stream()
                .map(orderItem -> {
                    InvoiceItem invoiceItem = new InvoiceItem();
                    invoiceItem.setProductId(orderItem.getProductId());
                    invoiceItem.setQty(Long.valueOf(orderItem.getQuantity()));
                    invoiceItem.setPriceAtPurchase(orderItem.getUnitPrice());
                    invoiceItem.setProductName(orderItem.getProductName());
                    invoiceItem.setInvoice(invoice);
                    invoiceItem.setSubtotal(orderItem.getSubtotal());

                    return invoiceItem;
                })
                .toList();

        invoiceItemRepository.saveAll(invoiceItems);

        return invoice.getId();
    }
}
