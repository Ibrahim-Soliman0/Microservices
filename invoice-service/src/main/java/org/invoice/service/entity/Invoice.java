package org.invoice.service.entity;

import jakarta.persistence.*;

import java.util.List;

@Entity
public class Invoice {

    @Id
    @GeneratedValue(strategy = GenerationType.SEQUENCE)
    private Long id;

    @Column(unique = true)
    private Long orderId;

    private Long userId;

    @OneToMany
    private List<InvoiceItem> invoiceItem;
}
