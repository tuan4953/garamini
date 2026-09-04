package com.garage.rescue.dto;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import lombok.*;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class RescueRequestDto {

    @NotNull(message = "Vui lòng chọn khách hàng")
    private Long customerId;

    private Long vehicleId;

    @NotBlank(message = "Vị trí sự cố không được để trống")
    private String location;

    private String description;
}