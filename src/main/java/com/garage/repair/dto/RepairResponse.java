package com.garage.repair.dto;

import com.garage.repair.model.RepairOrder.RepairStatus;
import lombok.*;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class RepairResponse {

    private Long id;
    private String orderCode;
    private Long vehicleId;
    private String licensePlate;
    private String vehicleModel;
    private Long customerId;
    private String customerName;
    private String customerPhone;
    private Long technicianId;
    private String technicianName;
    private RepairStatus status;
    private BigDecimal totalAmount;
    private String note;
    private LocalDateTime createdAt;
    private LocalDateTime completedAt;

    @Builder.Default
    private List<RepairItemResponse> items = new ArrayList<>();

    @Getter
    @Setter
    @NoArgsConstructor
    @AllArgsConstructor
    @Builder
    public static class RepairItemResponse {
        private Long id;
        private Long serviceId;
        private Long sparePartId;
        private String itemName;
        private String type; // SERVICE / SPARE_PART
        private Integer quantity;
        private BigDecimal unitPrice;
        private BigDecimal totalPrice;
    }
}