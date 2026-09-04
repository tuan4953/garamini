package com.garage.vehicle.dto;

import lombok.*;

import java.time.LocalDateTime;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class VehicleResponse {

    private Long id;
    private String licensePlate;
    private String brand;
    private String model;
    private String color;
    private Integer manufactureYear;
    private String chassisNumber;
    private String engineNumber;
    private String imageUrl;

    // Thông tin chủ xe
    private Long ownerId;
    private String ownerFullName;
    private String ownerPhone;

    private LocalDateTime createdAt;
    private LocalDateTime updatedAt;
}