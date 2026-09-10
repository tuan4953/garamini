package com.garage.repair.repository;

import com.garage.repair.model.RepairOrder;
import com.garage.repair.model.RepairOrder.RepairStatus;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.math.BigDecimal;
import java.util.List;
import java.util.Optional;

@Repository
public interface RepairOrderRepository extends JpaRepository<RepairOrder, Long> {

    Optional<RepairOrder> findByOrderCode(String orderCode);

    List<RepairOrder> findByCustomerIdOrderByCreatedAtDesc(Long customerId);

    List<RepairOrder> findByVehicleIdOrderByCreatedAtDesc(Long vehicleId);

    List<RepairOrder> findByTechnicianEmailOrderByCreatedAtDesc(String email);

    // Tìm theo status (không có keyword)
    Page<RepairOrder> findByStatusOrderByCreatedAtDesc(RepairStatus status, Pageable pageable);

    // Tìm theo keyword (không có status filter) - bỏ IS NULL tránh bug Hibernate 6
    @Query(value = "SELECT r FROM RepairOrder r " +
            "LEFT JOIN r.vehicle v " +
            "LEFT JOIN r.customer c " +
            "LEFT JOIN r.technician t " +
            "WHERE (" +
            "LOWER(r.orderCode) LIKE LOWER(CONCAT('%', :keyword, '%')) OR " +
            "LOWER(v.licensePlate) LIKE LOWER(CONCAT('%', :keyword, '%')) OR " +
            "LOWER(c.fullName) LIKE LOWER(CONCAT('%', :keyword, '%')) OR " +
            "LOWER(c.phone) LIKE LOWER(CONCAT('%', :keyword, '%')) OR " +
            "LOWER(t.fullName) LIKE LOWER(CONCAT('%', :keyword, '%')))",
            countQuery = "SELECT count(r) FROM RepairOrder r " +
            "LEFT JOIN r.vehicle v " +
            "LEFT JOIN r.customer c " +
            "LEFT JOIN r.technician t " +
            "WHERE (" +
            "LOWER(r.orderCode) LIKE LOWER(CONCAT('%', :keyword, '%')) OR " +
            "LOWER(v.licensePlate) LIKE LOWER(CONCAT('%', :keyword, '%')) OR " +
            "LOWER(c.fullName) LIKE LOWER(CONCAT('%', :keyword, '%')) OR " +
            "LOWER(c.phone) LIKE LOWER(CONCAT('%', :keyword, '%')) OR " +
            "LOWER(t.fullName) LIKE LOWER(CONCAT('%', :keyword, '%')))")
    Page<RepairOrder> searchByKeyword(@Param("keyword") String keyword, Pageable pageable);

    // Tìm theo cả status VÀ keyword
    @Query(value = "SELECT r FROM RepairOrder r " +
            "LEFT JOIN r.vehicle v " +
            "LEFT JOIN r.customer c " +
            "LEFT JOIN r.technician t " +
            "WHERE r.status = :status " +
            "AND (" +
            "LOWER(r.orderCode) LIKE LOWER(CONCAT('%', :keyword, '%')) OR " +
            "LOWER(v.licensePlate) LIKE LOWER(CONCAT('%', :keyword, '%')) OR " +
            "LOWER(c.fullName) LIKE LOWER(CONCAT('%', :keyword, '%')) OR " +
            "LOWER(c.phone) LIKE LOWER(CONCAT('%', :keyword, '%')) OR " +
            "LOWER(t.fullName) LIKE LOWER(CONCAT('%', :keyword, '%')))",
            countQuery = "SELECT count(r) FROM RepairOrder r " +
            "LEFT JOIN r.vehicle v " +
            "LEFT JOIN r.customer c " +
            "LEFT JOIN r.technician t " +
            "WHERE r.status = :status " +
            "AND (" +
            "LOWER(r.orderCode) LIKE LOWER(CONCAT('%', :keyword, '%')) OR " +
            "LOWER(v.licensePlate) LIKE LOWER(CONCAT('%', :keyword, '%')) OR " +
            "LOWER(c.fullName) LIKE LOWER(CONCAT('%', :keyword, '%')) OR " +
            "LOWER(c.phone) LIKE LOWER(CONCAT('%', :keyword, '%')) OR " +
            "LOWER(t.fullName) LIKE LOWER(CONCAT('%', :keyword, '%')))")
    Page<RepairOrder> searchByStatusAndKeyword(@Param("status") RepairStatus status,
                                               @Param("keyword") String keyword,
                                               Pageable pageable);

    long countByStatus(RepairStatus status);

    long countByStatusIn(List<RepairStatus> statuses);

    @Query("SELECT SUM(r.totalAmount) FROM RepairOrder r WHERE r.status = :status")
    BigDecimal sumTotalRevenueByStatus(@Param("status") RepairStatus status);

    @Query("SELECT COALESCE(SUM(r.totalAmount), 0) FROM RepairOrder r WHERE r.status IN :statuses")
    BigDecimal sumTotalRevenueByStatusIn(@Param("statuses") List<RepairStatus> statuses);
}