package com.garage.rescue.dto;

import com.garage.rescue.model.RescueRequest.RescueStatus;
import jakarta.validation.constraints.NotNull;
import lombok.*;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class RescueUpdateDto {

    @NotNull(message = "Vui lòng chọn trạng thái mới")
    private RescueStatus status;

    private Long technicianId;
    private String note;
}