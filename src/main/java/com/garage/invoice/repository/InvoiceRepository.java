package com.garage.invoice.repository;

import com.garage.invoice.model.Invoice;
import com.garage.invoice.model.Invoice.PaymentStatus;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.Optional;

@Repository
public interface InvoiceRepository extends JpaRepository<Invoice, Long> {

    Optional<Invoice> findByInvoiceNumber(String invoiceNumber);

    @Query(value = "SELECT i FROM Invoice i " +
            "LEFT JOIN FETCH i.customer c " +
            "LEFT JOIN FETCH i.vehicle v " +
            "WHERE (:status IS NULL OR i.paymentStatus = :status) AND " +
            "(:keyword IS NULL OR LOWER(i.invoiceNumber) LIKE LOWER(CONCAT('%', :keyword, '%')) OR " +
            "LOWER(c.fullName) LIKE LOWER(CONCAT('%', :keyword, '%')) OR " +
            "LOWER(c.phone) LIKE LOWER(CONCAT('%', :keyword, '%')))",
            countQuery = "SELECT COUNT(i) FROM Invoice i " +
                    "LEFT JOIN i.customer c " +
                    "WHERE (:status IS NULL OR i.paymentStatus = :status) AND " +
                    "(:keyword IS NULL OR LOWER(i.invoiceNumber) LIKE LOWER(CONCAT('%', :keyword, '%')) OR " +
                    "LOWER(c.fullName) LIKE LOWER(CONCAT('%', :keyword, '%')) OR " +
                    "LOWER(c.phone) LIKE LOWER(CONCAT('%', :keyword, '%')))")
    Page<Invoice> searchInvoices(@Param("status") PaymentStatus status,
                                 @Param("keyword") String keyword,
                                 Pageable pageable);
}