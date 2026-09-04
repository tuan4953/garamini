package com.garage.quotation.dto;

import com.garage.quotation.model.Quotation.QuotationStatus;
import lombok.*;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.List;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class QuotationResponse {

    private Long id;
    private String quotationCode;
    private Long vehicleId;
    private String licensePlate;
    private String vehicleModel;
    private Long customerId;
    private String customerName;
    private String customerPhone;
    private QuotationStatus status;
    private BigDecimal totalAmount;
    private String note;
    private LocalDateTime createdAt;
    private List<QuotationItemResponse> items;

    @Getter
    @Setter
    @NoArgsConstructor
    @AllArgsConstructor
    @Builder
    public static class QuotationItemResponse {
        private Long id;
        private String itemName;
        private String type;
        private Integer quantity;
        private BigDecimal unitPrice;
        private BigDecimal totalPrice;
    }
}