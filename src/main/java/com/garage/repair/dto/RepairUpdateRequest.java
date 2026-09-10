package com.garage.repair.dto;

import com.garage.repair.model.RepairOrder.RepairStatus;
import lombok.*;

import java.util.ArrayList;
import java.util.List;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class RepairUpdateRequest {

    private Long vehicleId;
    private Long customerId;
    private Long technicianId;
    private RepairStatus status;
    private String note;

    @Builder.Default
    private List<RepairRequest.RepairItemRequest> items = new ArrayList<>();
}