package com.garage.repair.dto;

import com.garage.repair.model.RepairOrder.RepairStatus;
import lombok.*;

import java.time.LocalDateTime;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class AssignedJobResponse {

    private Long orderId;
    private String orderCode;
    private String licensePlate;
    private String vehicleModel;
    private String customerName;
    private String customerPhone;
    private String description;
    private RepairStatus status;
    private LocalDateTime createdAt;
}