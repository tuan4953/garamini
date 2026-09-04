package com.garage.repair.model;

import com.garage.user.model.User;
import com.garage.vehicle.model.Vehicle;
import jakarta.persistence.*;
import lombok.*;

import java.time.LocalDateTime;

@Entity
@Table(name = "inspections")
@Getter @Setter @NoArgsConstructor @AllArgsConstructor @Builder
public class Inspection {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "vehicle_id", nullable = false)
    private Vehicle vehicle;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "technician_id")
    private User technician;

    @Column(columnDefinition = "TEXT")
    private String initialCondition; // Tình trạng xe khi tiếp nhận

    @Column(columnDefinition = "TEXT")
    private String diagnosticResult; // Kết quả chẩn đoán

    @Column(nullable = false)
    private LocalDateTime inspectionDate;

    @PrePersist
    protected void onCreate() {
        this.inspectionDate = LocalDateTime.now();
    }
}
