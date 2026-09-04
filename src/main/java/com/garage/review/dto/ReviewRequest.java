package com.garage.review.dto;

import jakarta.validation.constraints.*;
import lombok.*;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
public class ReviewRequest {

    @NotNull(message = "Vui lòng chọn hóa đơn/dịch vụ")
    private Long invoiceId;

    @NotBlank(message = "Tên khách hàng không được để trống")
    private String customerName;

    @NotNull(message = "Vui lòng chọn số sao")
    @Min(value = 1, message = "Đánh giá tối thiểu 1 sao")
    @Max(value = 5, message = "Đánh giá tối đa 5 sao")
    private Integer rating;

    @NotBlank(message = "Vui lòng nhập nội dung đánh giá")
    private String comment;
}