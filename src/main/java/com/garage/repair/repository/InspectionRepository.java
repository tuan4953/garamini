package com.garage.repair.repository;

import com.garage.repair.model.Inspection;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface InspectionRepository extends JpaRepository<Inspection, Long> {
    List<Inspection> findByVehicleIdOrderByInspectionDateDesc(Long vehicleId);
}