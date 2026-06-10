package org.invoice.service.controller;

import lombok.AllArgsConstructor;
import org.invoice.service.dto.OrderEvent;
import org.invoice.service.entity.Invoice;
import org.invoice.service.service.InvoiceService;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/invoices")
@AllArgsConstructor
public class InvoiceController {

    private InvoiceService invoiceService;

    @GetMapping("/{invoiceId}")
    public ResponseEntity<Invoice> getInvoice(@PathVariable Long invoiceId) {
        return ResponseEntity.ok(invoiceService.getInvoiceById(invoiceId));
    }

//    @GetMapping("/{invoiceId}/pdf")
//    public ResponseEntity<Invoice> getInvoice(@PathVariable Long invoiceId) {
//        return ResponseEntity.ok(invoiceService.getInvoiceById(invoiceId));
//    }

    @GetMapping("/user/{userId}")
    public ResponseEntity<List<Invoice>> getUserInvoices(@PathVariable Long userId) {

        return ResponseEntity.ok(invoiceService.getAllInvoicesByUserId(userId));
    }
}
