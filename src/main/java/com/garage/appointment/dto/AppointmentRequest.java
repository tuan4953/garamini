package com.garage.appointment.dto;

import jakarta.validation.constraints.Future;
import jakarta.validation.constraints.NotNull;
import lombok.*;
import org.springframework.format.annotation.DateTimeFormat;

import java.time.LocalDateTime;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class AppointmentRequest {

    @NotNull(message = "Vui lòng chọn xe cần bảo dưỡng/sửa chữa")
    private Long vehicleId;

    @NotNull(message = "Vui lòng chọn thời gian hẹn")
    @Future(message = "Thời gian hẹn phải ở trong tương lai")
    @DateTimeFormat(pattern = "yyyy-MM-dd'T'HH:mm")
    private LocalDateTime appointmentDate;

    private String notes;
}