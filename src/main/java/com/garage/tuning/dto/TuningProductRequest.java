package com.garage.tuning.dto;

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
public class TuningProductRequest {

    @NotBlank(message = "Tên gói độ/phụ kiện không được để trống")
    private String name;

    private String description;

    @NotNull(message = "Giá không được để trống")
    @Min(value = 0, message = "Giá tối thiểu là 0")
    private BigDecimal price;

    @NotNull(message = "Số lượng không được để trống")
    @Min(value = 0, message = "Số lượng không được âm")
    private Integer stockQuantity;

    private String imageUrl;
    private Long categoryId;
    private Boolean active;
}