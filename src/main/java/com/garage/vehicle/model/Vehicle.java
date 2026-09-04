package com.garage.vehicle.model;

import com.garage.user.model.User;
import jakarta.persistence.*;
import lombok.*;

import java.time.LocalDateTime;
@Entity
@Table(name = "vehicles")
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class Vehicle {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(name = "license_plate", nullable = false, unique = true, length = 20)
    private String licensePlate;

    @Column(nullable = false, length = 50)
    private String brand; // Ví dụ: Honda, Yamaha, Kawasaki

    @Column(nullable = false, length = 50)
    private String model; // Ví dụ: SH 150i, Exciter 155, Ninja 400

    @Column(length = 20)
    private String color;

    @Column(name = "manufacture_year")
    private Integer manufactureYear;

    @Column(name = "chassis_number", length = 50)
    private String chassisNumber; // Số khung

    @Column(name = "engine_number", length = 50)
    private String engineNumber; // Số máy

    @Column(name = "image_url")
    private String imageUrl;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "user_id", nullable = false)
    private User owner;

    @Column(name = "created_at")
    private LocalDateTime createdAt;

    @Column(name = "updated_at")
    private LocalDateTime updatedAt;

    @PrePersist
    protected void onCreate() {
        createdAt = LocalDateTime.now();
        updatedAt = LocalDateTime.now();
    }

    @PreUpdate
    protected void onUpdate() {
        updatedAt = LocalDateTime.now();
    }
}
