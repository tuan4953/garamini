package com.garage.repair.model;

import com.garage.user.model.User;
import com.garage.vehicle.model.Vehicle;
import jakarta.persistence.*;
import lombok.*;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;

@Entity
@Table(name = "repair_orders")
@Getter @Setter @NoArgsConstructor @AllArgsConstructor @Builder
public class RepairOrder {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(unique = true, nullable = false, length = 30)
    private String orderCode; // Mã đơn: RO-YYYYMMDD-XXX

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "vehicle_id", nullable = false)
    private Vehicle vehicle;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "customer_id", nullable = false)
    private User customer;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "technician_id")
    private User technician;

    @OneToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "inspection_id")
    private Inspection inspection;

    @Enumerated(EnumType.STRING)
    @Column(nullable = false, length = 30)
    private RepairStatus status; // RECEIVING, DIAGNOSING, IN_PROGRESS, COMPLETED, CANCELLED

    @Column(precision = 12, scale = 2)
    private BigDecimal totalAmount;

    @Column(columnDefinition = "TEXT")
    private String note;

    @OneToMany(mappedBy = "repairOrder", cascade = CascadeType.ALL, orphanRemoval = true)
    @Builder.Default
    private List<RepairItem> items = new ArrayList<>();

    private LocalDateTime createdAt;
    private LocalDateTime completedAt;

    @PrePersist
    protected void onCreate() {
        this.createdAt = LocalDateTime.now();
        if (this.status == null) this.status = RepairStatus.RECEIVING;
        if (this.totalAmount == null) this.totalAmount = BigDecimal.ZERO;
    }

    public enum RepairStatus {
        RECEIVING,
        RECEIVED,
        DIAGNOSING,
        IN_PROGRESS,
        COMPLETED,
        CANCELLED,
        PENDING,
        ACCEPTED,
        CONFIRMED,
        PROCESSING,
        REJECTED,
        WAITING,
        APPROVED,
        FIXING,
        ASSIGNED,
        WAITING_PARTS,
        FINISHED,
        DONE,
        PAID
    }
}