package com.garage.repair.repository;

import com.garage.repair.model.RepairOrder;
import com.garage.repair.model.RepairOrder.RepairStatus;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
public interface RepairOrderRepository extends JpaRepository<RepairOrder, Long> {

    Optional<RepairOrder> findByOrderCode(String orderCode);

    List<RepairOrder> findByCustomerIdOrderByCreatedAtDesc(Long customerId);

    List<RepairOrder> findByVehicleIdOrderByCreatedAtDesc(Long vehicleId);

    // MỚI BỔ SUNG: Tìm đơn sửa chữa theo Email của KTV được phân công
    List<RepairOrder> findByTechnicianEmailOrderByCreatedAtDesc(String email);

    @Query("SELECT r FROM RepairOrder r WHERE " +
            "(:status IS NULL OR r.status = :status) AND " +
            "(:keyword IS NULL OR LOWER(r.orderCode) LIKE LOWER(CONCAT('%', :keyword, '%')) OR " +
            "LOWER(r.vehicle.licensePlate) LIKE LOWER(CONCAT('%', :keyword, '%')) OR " +
            "LOWER(r.customer.fullName) LIKE LOWER(CONCAT('%', :keyword, '%')))")
    Page<RepairOrder> searchRepairOrders(@Param("status") RepairStatus status,
                                         @Param("keyword") String keyword,
                                         Pageable pageable);
}