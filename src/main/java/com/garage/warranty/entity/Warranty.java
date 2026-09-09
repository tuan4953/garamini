package com.garage.warranty.entity;

import jakarta.persistence.*;
import lombok.*;

import java.time.LocalDate;

@Entity
@Table(name = "warranties")
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class Warranty {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(nullable = false)
    private Long customerId;

    @Column(nullable = false)
    private String customerName;

    @Column(nullable = false)
    private String licensePlate;

    @Column(nullable = false)
    private String itemOrService; // Tên linh kiện / Dịch vụ bảo hành

    private LocalDate startDate;
    private LocalDate expiryDate;

    @Column(columnDefinition = "TEXT")
    private String notes;

    public boolean isActive() {
        return expiryDate != null && !expiryDate.isBefore(LocalDate.now());
    }
}