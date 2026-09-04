package com.garage.dashboard.service;

import com.garage.news.repository.NewsRepository;
import com.garage.promotion.repository.PromotionRepository;
import com.garage.review.repository.ReviewRepository;
import lombok.RequiredArgsConstructor;
import org.jspecify.annotations.Nullable;
import org.springframework.stereotype.Service;

import java.time.LocalDate;
import java.util.HashMap;
import java.util.Map;

@Service
@RequiredArgsConstructor
public class DashboardService {

    private final ReviewRepository reviewRepository;
    private final PromotionRepository promotionRepository;
    private final NewsRepository newsRepository;
    // Bạn có thể inject thêm InvoiceRepository, AppointmentRepository,... ở đây

    // Tổng quan thống kê cho Admin
    public Map<String, Object> getAdminStats() {
        Map<String, Object> stats = new HashMap<>();
        stats.put("totalReviews", reviewRepository.count());
        stats.put("avgRating", reviewRepository.getAverageRating());
        stats.put("activePromotions", promotionRepository.findActivePromotions(LocalDate.now()).size());
        stats.put("publishedNews", newsRepository.findByPublishedTrueOrderByCreatedAtDesc().size());

        // Dữ liệu mô phỏng cho Garage
        stats.put("totalRevenue", 125000000.0); // 125 triệu VNĐ
        stats.put("completedInvoices", 48);
        stats.put("pendingAppointments", 12);
        return stats;
    }

    // Thống kê công việc cho Kỹ thuật viên (Technician)
    public Map<String, Object> getTechnicianStats(Long technicianId) {
        Map<String, Object> stats = new HashMap<>();
        stats.put("assignedTasksToday", 5);
        stats.put("completedTasksToday", 3);
        stats.put("pendingTasks", 2);
        stats.put("monthlyCompleted", 42);
        return stats;
    }

    // Thống kê cá nhân cho Khách hàng (Customer)
    public Map<String, Object> getCustomerStats(String customerPhone) {
        Map<String, Object> stats = new HashMap<>();
        stats.put("totalAppointments", 4);
        stats.put("activeAppointments", 1);
        stats.put("totalSpent", 3500000.0);
        stats.put("availableVouchers", promotionRepository.findActivePromotions(LocalDate.now()).size());
        return stats;
    }

    public Map<String, Object> getDashboardStatsMap() {
        return getAdminStats();
    }
}