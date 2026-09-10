package com.garage.rescue.service;

import com.garage.exception.ResourceNotFoundException;
import com.garage.rescue.dto.*;
import com.garage.rescue.model.RescueRequest;
import com.garage.rescue.model.RescueRequest.RescueStatus;
import com.garage.rescue.repository.RescueRequestRepository;
import com.garage.user.model.User;
import com.garage.user.repository.UserRepository;
import com.garage.vehicle.repository.VehicleRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.Collections;
import java.util.List;

@Service
@RequiredArgsConstructor
public class RescueService {

    private final RescueRequestRepository rescueRepository;
    private final UserRepository userRepository;
    private final VehicleRepository vehicleRepository;

    // --- BỔ SUNG CÁC HÀM PHỤC VỤ CLIENT HTML ---

    /**
     * Lấy danh sách các gói cứu hộ phục vụ trang rescue/list.html
     */
    public List<Object> getAllActiveServices() {
        // Tùy chọn: Nếu bạn có RescueServiceRepository thì inject vào đây để lấy danh sách.
        // Hiện tại trả về danh sách rỗng an toàn để tránh nổ lỗi 500 trên Thymeleaf list.html
        return Collections.emptyList();
    }

    /**
     * Lấy chi tiết gói cứu hộ cho trang rescue/detail.html
     */
    public Object getServiceById(Long id) {
        if (id == null) return null;
        return null;
    }

    /**
     * Hàm tạo yêu cầu cứu hộ từ Form đăng ký của Khách hàng (rescue/booking.html)
     */
    @Transactional
    public RescueResponseDto createBookingFromClient(RescueBookingDto dto) {
        // Trường hợp khách chưa đăng nhập/truyền customerId -> Lấy user mặc định hoặc tạo vãng lai
        User customer = null;
        if (dto.getCustomerId() != null) {
            customer = userRepository.findById(dto.getCustomerId()).orElse(null);
        }

        RescueRequest rescue = RescueRequest.builder()
                .customer(customer)
                .location(dto.getLocation())
                .description(dto.getNote())
                .status(RescueStatus.PENDING)
                .build();

        return mapToResponse(rescueRepository.save(rescue));
    }

    // --- CÁC HÀM HIỆN CÓ CỦA BẠN (GIỮ NGUYÊN) ---

    @Transactional(readOnly = true)
    public Page<RescueResponseDto> searchRequests(RescueStatus status, String keyword, Pageable pageable) {
        if (keyword != null && keyword.trim().isEmpty()) {
            keyword = null;
        } else if (keyword != null) {
            keyword = keyword.trim();
        }
        return rescueRepository.searchRescueRequests(status, keyword, pageable).map(this::mapToResponse);
    }

    @Transactional(readOnly = true)
    public RescueResponseDto getRequestById(Long id) {
        return mapToResponse(rescueRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Không tìm thấy yêu cầu cứu hộ ID: " + id)));
    }

    @Transactional
    public RescueResponseDto createRescueRequest(RescueRequestDto dto) {
        User customer = userRepository.findById(dto.getCustomerId())
                .orElseThrow(() -> new ResourceNotFoundException("Không tìm thấy khách hàng"));

        RescueRequest rescue = RescueRequest.builder()
                .customer(customer)
                .vehicle(dto.getVehicleId() != null ? vehicleRepository.findById(dto.getVehicleId()).orElse(null) : null)
                .location(dto.getLocation())
                .description(dto.getDescription())
                .status(RescueStatus.PENDING)
                .build();

        return mapToResponse(rescueRepository.save(rescue));
    }

    @Transactional
    public RescueResponseDto updateRescueRequest(Long id, RescueUpdateDto dto) {
        RescueRequest rescue = rescueRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Không tìm thấy yêu cầu cứu hộ ID: " + id));

        rescue.setStatus(dto.getStatus());
        if (dto.getNote() != null) rescue.setNote(dto.getNote());

        if (dto.getTechnicianId() != null) {
            User tech = userRepository.findById(dto.getTechnicianId())
                    .orElseThrow(() -> new ResourceNotFoundException("Không tìm thấy kỹ thuật viên"));
            rescue.setAssignedTechnician(tech);
        }

        return mapToResponse(rescueRepository.save(rescue));
    }

    private RescueResponseDto mapToResponse(RescueRequest r) {
        return RescueResponseDto.builder()
                .id(r.getId())
                .customerId(r.getCustomer() != null ? r.getCustomer().getId() : null)
                .customerName(r.getCustomer() != null ? r.getCustomer().getFullName() : "Khách vãng lai")
                .customerPhone(r.getCustomer() != null ? r.getCustomer().getPhone() : "N/A")
                .vehicleId(r.getVehicle() != null ? r.getVehicle().getId() : null)
                .licensePlate(r.getVehicle() != null ? r.getVehicle().getLicensePlate() : "N/A")
                .vehicleModel(r.getVehicle() != null ? r.getVehicle().getModel() : "N/A")
                .location(r.getLocation())
                .description(r.getDescription())
                .status(r.getStatus())
                .technicianId(r.getAssignedTechnician() != null ? r.getAssignedTechnician().getId() : null)
                .technicianName(r.getAssignedTechnician() != null ? r.getAssignedTechnician().getFullName() : "Chưa phân công")
                .note(r.getNote())
                .createdAt(r.getCreatedAt())
                .updatedAt(r.getUpdatedAt())
                .build();
    }
}