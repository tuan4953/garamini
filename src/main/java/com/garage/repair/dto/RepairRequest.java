package com.garage.repair.dto;

import com.garage.repair.model.RepairOrder.RepairStatus;
import jakarta.validation.constraints.NotNull;
import lombok.*;

import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.List;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class RepairRequest {

    @NotNull(message = "Chưa chọn xe")
    private Long vehicleId;

    private Long customerId;
    private Long technicianId;
    private Long inspectionId;
    private RepairStatus status;
    private String note;

    @Builder.Default
    private List<RepairItemRequest> items = new ArrayList<>();

    @Getter
    @Setter
    @NoArgsConstructor
    @AllArgsConstructor
    @Builder
    public static class RepairItemRequest {
        private Long serviceId;
        private Long sparePartId;
        private String itemName;
        private String type; // SERVICE / SPARE_PART
        private Integer quantity;
        private BigDecimal unitPrice;
    }
}