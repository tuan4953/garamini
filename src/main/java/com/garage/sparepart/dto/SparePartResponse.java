package com.garage.sparepart.dto;

import lombok.*;

import java.math.BigDecimal;
import java.time.LocalDateTime;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class SparePartResponse {

    private Long id;
    private String name;
    private String partCode;
    private String description;
    private BigDecimal price;
    private Integer stockQuantity;
    private String unit;
    private Boolean active;
    private LocalDateTime createdAt;
    private LocalDateTime updatedAt;
}