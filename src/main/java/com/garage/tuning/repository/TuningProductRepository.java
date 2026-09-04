package com.garage.tuning.repository;

import com.garage.tuning.model.TuningProduct;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

@Repository
public interface TuningProductRepository extends JpaRepository<TuningProduct, Long> {

    @Query("SELECT p FROM TuningProduct p WHERE " +
            "(:categoryId IS NULL OR p.category.id = :categoryId) AND " +
            "(:active IS NULL OR p.active IS NULL OR p.active = :active) AND " +
            "(:keyword IS NULL OR LOWER(p.name) LIKE LOWER(CONCAT('%', :keyword, '%')))")
    Page<TuningProduct> searchProducts(@Param("categoryId") Long categoryId,
                                       @Param("active") Boolean active,
                                       @Param("keyword") String keyword,
                                       Pageable pageable);
}