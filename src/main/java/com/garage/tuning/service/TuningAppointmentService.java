package com.garage.tuning.service;

import com.garage.appointment.model.Appointment;
import com.garage.appointment.repository.AppointmentRepository;
import com.garage.exception.ResourceNotFoundException;
import com.garage.tuning.dto.TuningAppointmentRequest;
import com.garage.user.model.User;
import com.garage.user.repository.UserRepository;
import com.garage.vehicle.model.Vehicle;
import com.garage.vehicle.repository.VehicleRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
@RequiredArgsConstructor
public class TuningAppointmentService {

    private final AppointmentRepository appointmentRepository;
    private final UserRepository userRepository;
    private final VehicleRepository vehicleRepository;

    @Transactional
    public void createTuningAppointment(TuningAppointmentRequest request) {
        Appointment.AppointmentBuilder builder = Appointment.builder()
                .appointmentDate(request.getAppointmentDate())
                .serviceType("TUNING")
                .status(Appointment.AppointmentStatus.PENDING)
                .notes(request.getNote());

        // Kiểm tra tránh bị crash lỗi 500 do Null ID
        if (request.getCustomerId() != null) {
            User customer = userRepository.findById(request.getCustomerId())
                    .orElseThrow(() -> new ResourceNotFoundException("Không tìm thấy khách hàng"));
            builder.customer(customer);
        }

        if (request.getVehicleId() != null) {
            Vehicle vehicle = vehicleRepository.findById(request.getVehicleId())
                    .orElseThrow(() -> new ResourceNotFoundException("Không tìm thấy xe"));
            builder.vehicle(vehicle);
        }

        appointmentRepository.save(builder.build());
    }

    @Transactional
    public void createAppointment(TuningAppointmentRequest request) {
        createTuningAppointment(request);
    }
}