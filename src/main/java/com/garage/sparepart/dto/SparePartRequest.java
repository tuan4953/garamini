package com.garage.sparepart.dto;

import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import lombok.*;

import java.math.BigDecimal;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class SparePartRequest {

    @NotBlank(message = "Tên phụ tùng không được để trống")
    private String name;

    private String description;

    @NotNull(message = "Giá phụ tùng không được để trống")
    @Min(value = 0, message = "Giá không được nhỏ hơn 0")
    private BigDecimal price;

    @NotNull(message = "Số lượng tồn kho không được để trống")
    @Min(value = 0, message = "Số lượng tồn kho không được nhỏ hơn 0")
    private Integer stockQuantity;

    private String unit;

    private Boolean active;
}