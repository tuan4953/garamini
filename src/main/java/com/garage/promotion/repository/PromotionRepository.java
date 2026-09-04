package com.garage.promotion.repository;

import com.garage.promotion.model.Promotion;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.time.LocalDate;
import java.util.List;
import java.util.Optional;

@Repository
public interface PromotionRepository extends JpaRepository<Promotion, Long> {

    Optional<Promotion> findByCodeIgnoreCase(String code);

    // Lấy các mã khuyến mãi còn hạn và active cho Client
    @Query("SELECT p FROM Promotion p WHERE p.active = true AND :today BETWEEN p.startDate AND p.endDate ORDER BY p.endDate ASC")
    List<Promotion> findActivePromotions(@Param("today") LocalDate today);

    // Lấy danh sách sắp xếp theo ngày tạo cho Admin
    List<Promotion> findAllByOrderByCreatedAtDesc();

    boolean existsByCodeIgnoreCase(String code);
}