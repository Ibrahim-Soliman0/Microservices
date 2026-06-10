package org.invoice.service.repository;

import org.invoice.service.entity.Invoice;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
public interface InvoiceRepository extends JpaRepository<Invoice, Long> {

    List<Invoice> getAllByUserId(Long id);

    Optional<Invoice> findByOrderId(Long orderId);

}
