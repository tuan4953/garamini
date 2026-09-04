package com.garage.tuning.repository;

import com.garage.tuning.model.TuningCategory;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface TuningCategoryRepository extends JpaRepository<TuningCategory, Long> {
    List<TuningCategory> findByActiveTrueOrderByNameAsc();
}