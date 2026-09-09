package com.garage.quotation.model;

import com.garage.user.model.User;
import com.garage.vehicle.model.Vehicle;
import jakarta.persistence.*;
import lombok.*;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;

@Entity
@Table(name = "quotations")
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class Quotation {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(nullable = false, unique = true, length = 50)
    private String quotationCode;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "vehicle_id", nullable = false)
    private Vehicle vehicle;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "customer_id", nullable = false)
    private User customer;

    @Enumerated(EnumType.STRING)
    @Column(nullable = false, length = 30)
    private QuotationStatus status;

    @Column(precision = 12, scale = 2)
    private BigDecimal totalAmount;

    @Column(columnDefinition = "TEXT")
    private String note;

    @OneToMany(mappedBy = "quotation", cascade = CascadeType.ALL, orphanRemoval = true)
    @Builder.Default
    private List<QuotationItem> items = new ArrayList<>();

    private LocalDateTime createdAt;
    private LocalDateTime updatedAt;

    @PrePersist
    protected void onCreate() {
        this.createdAt = LocalDateTime.now();
        this.updatedAt = LocalDateTime.now();
        if (this.status == null) this.status = QuotationStatus.PENDING;
        if (this.totalAmount == null) this.totalAmount = BigDecimal.ZERO;
    }

    @PreUpdate
    protected void onUpdate() {
        this.updatedAt = LocalDateTime.now();
    }

    // ✅ CẬP NHẬT THÊM APPROVED VÀO ĐÂY:
    public enum QuotationStatus {
        DRAFT,      // Nháp
        PENDING,    // Chờ xử lý / Chờ duyệt
        SENT,       // Đã gửi cho khách hàng
        APPROVED,   // Đã duyệt
        ACCEPTED,   // Khách hàng đồng ý
        REJECTED,   // Từ chối
        EXPIRED,    // Hết hạn
        COMPLETED   // Hoàn thành
    }
}