package com.garage.appointment.dto;


import com.garage.appointment.model.Appointment;
import jakarta.validation.constraints.NotNull;
import lombok.*;
import org.springframework.format.annotation.DateTimeFormat;

import java.time.LocalDateTime;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class AppointmentUpdateRequest {

    @NotNull(message = "ID lịch hẹn không được để trống")
    private Long id;

    @NotNull(message = "Vui lòng chọn thời gian")
    @DateTimeFormat(pattern = "yyyy-MM-dd'T'HH:mm")
    private LocalDateTime appointmentDate;

    @NotNull(message = "Trạng thái không được để trống")
    private Appointment.AppointmentStatus status;

    private String notes;
    private String cancellationReason;
}