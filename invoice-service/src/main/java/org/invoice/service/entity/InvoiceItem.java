package org.invoice.service.entity;

import jakarta.persistence.*;

@Entity
public class InvoiceItem {

    @Id
    @GeneratedValue(strategy = GenerationType.SEQUENCE)
    private Long id;

    private Long productId;

    private Long qty;

    private Double priceAtPurchase;

    @ManyToOne(cascade = CascadeType.ALL)
    private Invoice invoice;
}
