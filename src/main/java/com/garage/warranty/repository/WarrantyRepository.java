package com.garage.warranty.repository;

import com.garage.warranty.entity.Warranty;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface WarrantyRepository extends JpaRepository<Warranty, Long> {
    List<Warranty> findByCustomerId(Long customerId);
}