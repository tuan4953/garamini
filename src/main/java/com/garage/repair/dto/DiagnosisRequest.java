package com.garage.repair.dto;

import lombok.*;

import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.List;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class DiagnosisRequest {

    private Long repairOrderId;
    private String diagnosticResult; // Kết quả chẩn đoán chi tiết
    private String technicianNote;    // Ghi chú của KTV

    // Danh sách hạng mục công việc/phụ tùng đề xuất
    private List<DiagnosisItemRequest> items = new ArrayList<>();

    @Getter
    @Setter
    @NoArgsConstructor
    @AllArgsConstructor
    @Builder
    public static class DiagnosisItemRequest {
        private String itemName;
        private String type; // SERVICE (Dịch vụ) hoặc SPARE_PART (Phụ tùng)
        private Integer quantity;
        private BigDecimal unitPrice;
    }
}