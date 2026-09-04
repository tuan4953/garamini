package com.garage.repair.dto;

import jakarta.validation.constraints.NotNull;
import lombok.*;

import java.math.BigDecimal;
import java.util.List;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class RepairRequest {

    @NotNull(message = "Chưa chọn xe")
    private Long vehicleId;

    @NotNull(message = "Chưa chọn khách hàng")
    private Long customerId;

    private Long technicianId;
    private Long inspectionId;
    private String note;
    private List<RepairItemRequest> items;

    @Getter
    @Setter
    @NoArgsConstructor
    @AllArgsConstructor
    @Builder
    public static class RepairItemRequest {
        private Long serviceId;
        private Long sparePartId;
        private Integer quantity;
        private BigDecimal unitPrice;
    }
}