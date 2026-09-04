package com.garage.repair.repository;

import com.garage.repair.model.RepairItem;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface RepairItemRepository extends JpaRepository<RepairItem, Long> {
    List<RepairItem> findByRepairOrderId(Long repairOrderId);
}