package com.garage.appointment.dto;


import com.garage.appointment.model.Appointment;
import lombok.*;

import java.time.LocalDateTime;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class AppointmentResponse {

    private Long id;

    // Thông tin khách hàng
    private Long customerId;
    private String customerName;
    private String customerPhone;

    // Thông tin phương tiện
    private Long vehicleId;
    private String licensePlate;
    private String vehicleBrand;
    private String vehicleModel;

    private LocalDateTime appointmentDate;
    private Appointment.AppointmentStatus status;
    private String notes;
    private String cancellationReason;
    private LocalDateTime createdAt;
    private LocalDateTime updatedAt;
}