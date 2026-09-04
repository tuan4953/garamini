package com.garage.news.repository;

import com.garage.news.model.News;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface NewsRepository extends JpaRepository<News, Long> {

    // Lấy danh sách tin tức đã xuất bản (mới nhất lên đầu)
    List<News> findByPublishedTrueOrderByCreatedAtDesc();

    // Lấy toàn bộ bài viết cho trang Admin quản lý
    List<News> findAllByOrderByCreatedAtDesc();
}
