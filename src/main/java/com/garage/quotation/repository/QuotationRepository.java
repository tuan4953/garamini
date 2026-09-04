package com.garage.quotation.repository;

import com.garage.quotation.model.Quotation;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

@Repository
public interface QuotationRepository extends JpaRepository<Quotation, Long> {

    @Query("SELECT q FROM Quotation q WHERE " +
            "(:status IS NULL OR q.status = :status) AND " +
            "(:keyword IS NULL OR LOWER(q.quotationCode) LIKE LOWER(CONCAT('%', :keyword, '%')) OR " +
            "LOWER(q.vehicle.licensePlate) LIKE LOWER(CONCAT('%', :keyword, '%')) OR " +
            "LOWER(q.customer.fullName) LIKE LOWER(CONCAT('%', :keyword, '%')))")
    Page<Quotation> searchQuotations(@Param("status") Quotation.QuotationStatus status,
                                     @Param("keyword") String keyword,
                                     Pageable pageable);
}