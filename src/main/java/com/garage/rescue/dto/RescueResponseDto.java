package com.garage.rescue.dto;

import com.garage.rescue.model.RescueRequest.RescueStatus;
import lombok.*;

import java.time.LocalDateTime;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class RescueResponseDto {

    private Long id;
    private Long customerId;
    private String customerName;
    private String customerPhone;
    private Long vehicleId;
    private String licensePlate;
    private String vehicleModel;
    private String location;
    private String description;
    private RescueStatus status;
    private Long technicianId;
    private String technicianName;
    private String note;
    private LocalDateTime createdAt;
    private LocalDateTime updatedAt;
}