package com.garage.appointment.service;

import com.garage.appointment.dto.AppointmentRequest;
import com.garage.appointment.dto.AppointmentResponse;
import com.garage.appointment.dto.AppointmentUpdateRequest;
import com.garage.appointment.model.Appointment;
import com.garage.appointment.repository.AppointmentRepository;
import com.garage.exception.BusinessException;
import com.garage.exception.ResourceNotFoundException;
import com.garage.service.repository.ServiceRepository;
import com.garage.user.model.User;
import com.garage.user.repository.UserRepository;
import com.garage.vehicle.model.Vehicle;
import com.garage.vehicle.repository.VehicleRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;
import java.util.List;
import java.util.stream.Collectors;

@org.springframework.stereotype.Service
@RequiredArgsConstructor
public class AppointmentService {

    private final AppointmentRepository appointmentRepository;
    private final UserRepository userRepository;
    private final VehicleRepository vehicleRepository;
    private final ServiceRepository serviceRepository;

    public List<AppointmentResponse> getAppointmentsByCustomer(Long customerId) {
        return appointmentRepository.findByCustomerIdOrderByAppointmentDateDesc(customerId)
                .stream()
                .map(this::mapToResponse)
                .collect(Collectors.toList());
    }

    public AppointmentResponse getAppointmentById(Long id) {
        Appointment appointment = appointmentRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Không tìm thấy lịch hẹn với ID: " + id));
        return mapToResponse(appointment);
    }

    public Page<AppointmentResponse> getAllAppointmentsPaged(Appointment.AppointmentStatus status, String keyword, Pageable pageable) {
        return appointmentRepository.searchAppointments(status, keyword, pageable)
                .map(this::mapToResponse);
    }

    @Transactional
    public AppointmentResponse createAppointment(Long customerId, AppointmentRequest request) {
        User customer = userRepository.findById(customerId)
                .orElseThrow(() -> new ResourceNotFoundException("Không tìm thấy thông tin khách hàng"));

        Vehicle vehicle = vehicleRepository.findById(request.getVehicleId())
                .orElseThrow(() -> new ResourceNotFoundException("Không tìm thấy thông tin xe"));

        com.garage.service.model.Service service = serviceRepository.findById(request.getServiceId())
                .orElseThrow(() -> new ResourceNotFoundException("Không tìm thấy dịch vụ"));

        if (!vehicle.getOwner().getId().equals(customerId)) {
            throw new BusinessException("Phương tiện này không thuộc quyền sở hữu của bạn");
        }

        LocalDateTime start = request.getAppointmentDate().minusMinutes(30);
        LocalDateTime end = request.getAppointmentDate().plusMinutes(30);
        boolean exists = appointmentRepository.existsByVehicleIdAndAppointmentDateBetweenAndStatusNot(
                vehicle.getId(), start, end, Appointment.AppointmentStatus.CANCELLED);

        if (exists) {
            throw new BusinessException("Xe này đã có lịch hẹn trong khung giờ gần kề. Vui lòng chọn thời gian khác");
        }

        Appointment appointment = Appointment.builder()
                .customer(customer)
                .vehicle(vehicle)
                .service(service)
                .appointmentDate(request.getAppointmentDate())
                .status(Appointment.AppointmentStatus.PENDING)
                .notes(request.getNotes())
                .build();

        return mapToResponse(appointmentRepository.save(appointment));
    }

    @Transactional
    public AppointmentResponse updateAppointment(AppointmentUpdateRequest request) {
        Appointment appointment = appointmentRepository.findById(request.getId())
                .orElseThrow(() -> new ResourceNotFoundException("Không tìm thấy lịch hẹn"));

        appointment.setAppointmentDate(request.getAppointmentDate());
        appointment.setStatus(request.getStatus());
        appointment.setNotes(request.getNotes());

        if (request.getStatus() == Appointment.AppointmentStatus.CANCELLED) {
            appointment.setCancellationReason(request.getCancellationReason());
        }

        return mapToResponse(appointmentRepository.save(appointment));
    }

    @Transactional
    public void cancelAppointment(Long appointmentId, Long customerId, String reason) {
        Appointment appointment = appointmentRepository.findById(appointmentId)
                .orElseThrow(() -> new ResourceNotFoundException("Không tìm thấy lịch hẹn"));

        if (!appointment.getCustomer().getId().equals(customerId)) {
            throw new BusinessException("Bạn không có quyền hủy lịch hẹn này");
        }

        if (appointment.getStatus() == Appointment.AppointmentStatus.COMPLETED || appointment.getStatus() == Appointment.AppointmentStatus.IN_PROGRESS) {
            throw new BusinessException("Không thể hủy lịch hẹn đã được tiến hành hoặc hoàn thành");
        }

        appointment.setStatus(Appointment.AppointmentStatus.CANCELLED);
        appointment.setCancellationReason(reason);
        appointmentRepository.save(appointment);
    }

    private AppointmentResponse mapToResponse(Appointment appointment) {
        return AppointmentResponse.builder()
                .id(appointment.getId())
                .customerId(appointment.getCustomer().getId())
                .customerName(appointment.getCustomer().getFullName())
                .customerPhone(appointment.getCustomer().getPhone())
                .vehicleId(appointment.getVehicle().getId())
                .licensePlate(appointment.getVehicle().getLicensePlate())
                .vehicleBrand(appointment.getVehicle().getBrand())
                .vehicleModel(appointment.getVehicle().getModel())
                .serviceName(appointment.getService() != null ? appointment.getService().getName() : null)
                .appointmentDate(appointment.getAppointmentDate())
                .status(appointment.getStatus())
                .notes(appointment.getNotes())
                .cancellationReason(appointment.getCancellationReason())
                .createdAt(appointment.getCreatedAt())
                .updatedAt(appointment.getUpdatedAt())
                .build();
    }
}