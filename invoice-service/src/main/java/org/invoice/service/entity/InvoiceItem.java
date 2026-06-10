package org.invoice.service.entity;

import jakarta.persistence.*;
import lombok.*;

@Entity
@Table(name = "invoice_item")
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class InvoiceItem {

  @Id
  @GeneratedValue(strategy = GenerationType.IDENTITY)
  private Long id;

  private Long productId;

  private String productName;

  private Long qty;

  private Double priceAtPurchase;

  private Double subtotal;

  @ManyToOne(cascade = CascadeType.ALL)
  private Invoice invoice;
}
