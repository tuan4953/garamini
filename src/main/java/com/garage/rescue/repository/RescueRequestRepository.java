package com.garage.rescue.repository;

import com.garage.rescue.model.RescueRequest;
import com.garage.rescue.model.RescueRequest.RescueStatus;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

@Repository
public interface RescueRequestRepository extends JpaRepository<RescueRequest, Long> {

    @Query("SELECT r FROM RescueRequest r " +
            "LEFT JOIN r.customer c " +
            "LEFT JOIN r.vehicle v " +
            "WHERE (:status IS NULL OR r.status = :status) AND " +
            "(:keyword IS NULL OR " +
            "LOWER(r.location) LIKE LOWER(CONCAT('%', :keyword, '%')) OR " +
            "LOWER(c.fullName) LIKE LOWER(CONCAT('%', :keyword, '%')) OR " +
            "LOWER(c.phone) LIKE LOWER(CONCAT('%', :keyword, '%')) OR " +
            "LOWER(v.licensePlate) LIKE LOWER(CONCAT('%', :keyword, '%')))")
    Page<RescueRequest> searchRescueRequests(@Param("status") RescueStatus status,
                                             @Param("keyword") String keyword,
                                             Pageable pageable);
}