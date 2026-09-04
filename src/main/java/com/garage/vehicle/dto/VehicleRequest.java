package com.garage.vehicle.dto;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Size;
import lombok.*;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class VehicleRequest {

    private Long id;

    @NotBlank(message = "Biển số xe không được để trống")
    @Size(max = 20, message = "Biển số xe không quá 20 ký tự")
    private String licensePlate;

    @NotBlank(message = "Hãng xe không được để trống")
    private String brand;

    @NotBlank(message = "Dòng xe không được để trống")
    private String model;

    private String color;

    private Integer manufactureYear;

    private String chassisNumber;

    private String engineNumber;

    private String imageUrl;

    @NotNull(message = "ID người sở hữu không được để trống")
    private Long ownerId;
}
