package com.garage.payment.dto;

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
public class PaymentRequest {

    @NotNull(message = "Vui lòng chọn hóa đơn thanh toán")
    private Long invoiceId;

    @NotNull(message = "Số tiền thanh toán không được để trống")
    @Min(value = 1, message = "Số tiền thanh toán phải lớn hơn 0")
    private BigDecimal amount;

    @NotBlank(message = "Phương thức thanh toán không được để trống")
    private String paymentMethod;

    private String note;
}