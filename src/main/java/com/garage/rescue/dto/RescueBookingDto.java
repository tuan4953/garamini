package com.garage.rescue.dto;

import jakarta.validation.constraints.NotBlank;
import lombok.Data;

@Data
public class RescueBookingDto {

    // ID khách hàng (dùng nếu khách đã đăng nhập)
    private Long customerId;

    private Long serviceId;

    @NotBlank(message = "Vui lòng nhập họ và tên")
    private String customerName;

    @NotBlank(message = "Vui lòng nhập số điện thoại")
    private String phoneNumber;

    @NotBlank(message = "Vui lòng nhập biển số xe")
    private String licensePlate;

    private String carModel;

    @NotBlank(message = "Vui lòng nhập vị trí sự cố")
    private String location;

    private String note;
}