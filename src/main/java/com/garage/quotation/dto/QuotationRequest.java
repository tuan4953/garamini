package com.garage.quotation.dto;

import jakarta.validation.constraints.NotNull;
import lombok.*;

import java.util.List;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class QuotationRequest {

    @NotNull(message = "Vui lòng chọn phương tiện")
    private Long vehicleId;

    @NotNull(message = "Vui lòng chọn khách hàng")
    private Long customerId;

    private String note;
    private List<QuotationItemRequest> items;
}