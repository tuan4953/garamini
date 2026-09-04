package com.garage.invoice.dto;

import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.NotNull;
import lombok.*;

import java.math.BigDecimal;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class InvoiceRequest {

    @NotNull(message = "Vui lòng chọn khách hàng")
    private Long customerId;

    private Long vehicleId;
    private Long repairOrderId;

    @NotNull(message = "Số tiền gốc không được để trống")
    @Min(value = 0, message = "Số tiền không được âm")
    private BigDecimal amount;

    private BigDecimal discount;
    private String paymentMethod;
    private String note;
}