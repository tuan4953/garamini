package com.garage.sparepart.repository;

import com.garage.sparepart.model.SparePart;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface SparePartRepository extends JpaRepository<SparePart, Long> {

    List<SparePart> findByActiveTrueOrderByNameAsc();

    boolean existsByName(String name);

    boolean existsByNameAndIdNot(String name, Long id);

    @Query("SELECT s FROM SparePart s WHERE " +
            "(:active IS NULL OR s.active = :active) AND " +
            "(:keyword IS NULL OR LOWER(s.name) LIKE LOWER(CONCAT('%', :keyword, '%')))")
    Page<SparePart> searchSpareParts(@Param("active") Boolean active,
                                     @Param("keyword") String keyword,
                                     Pageable pageable);
}