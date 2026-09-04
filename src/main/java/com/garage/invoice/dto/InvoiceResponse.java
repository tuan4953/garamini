package com.garage.invoice.dto;

import com.garage.invoice.model.Invoice.PaymentStatus;
import lombok.*;

import java.math.BigDecimal;
import java.time.LocalDateTime;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class InvoiceResponse {

    private Long id;
    private String invoiceNumber;
    private Long customerId;
    private String customerName;
    private String customerPhone;
    private Long vehicleId;
    private String licensePlate;
    private Long repairOrderId;
    private BigDecimal amount;
    private BigDecimal discount;
    private BigDecimal totalAmount;
    private PaymentStatus paymentStatus;
    private String paymentMethod;
    private String note;
    private LocalDateTime issuedAt;
    private LocalDateTime paidAt;
}