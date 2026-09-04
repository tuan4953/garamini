package com.garage.review.repository;

import com.garage.review.model.Review;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface ReviewRepository extends JpaRepository<Review, Long> {

    // Lấy danh sách review hiển thị công khai (mới nhất xếp trước)
    List<Review> findByVisibleTrueOrderByCreatedAtDesc();

    // Tính số sao trung bình của các review hiển thị
    @Query("SELECT COALESCE(AVG(r.rating), 0.0) FROM Review r WHERE r.visible = true")
    Double getAverageRating();

    // Lấy toàn bộ review cho trang Admin quản lý
    List<Review> findAllByOrderByCreatedAtDesc();

    boolean existsByInvoiceId(Long invoiceId);
}