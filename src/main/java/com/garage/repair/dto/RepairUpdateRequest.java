package com.garage.repair.dto;

import com.garage.repair.model.RepairOrder.RepairStatus;
import lombok.*;

import java.util.List;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class RepairUpdateRequest {

    private RepairStatus status;
    private Long technicianId;
    private String note;
    private List<RepairRequest.RepairItemRequest> items;
}