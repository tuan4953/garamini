package com.garage.repair.service;

import com.garage.exception.BusinessException;
import com.garage.exception.ResourceNotFoundException;
import com.garage.repair.dto.*;
import com.garage.repair.model.*;
import com.garage.repair.repository.*;
import com.garage.service.repository.ServiceRepository;

import com.garage.sparepart.dto.SparePartRequest;
import com.garage.sparepart.repository.SparePartRepository;
import com.garage.user.repository.UserRepository;
import com.garage.vehicle.repository.VehicleRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.UUID;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class RepairService {

    private final RepairOrderRepository repairOrderRepository;
    private final InspectionRepository inspectionRepository;
    private final VehicleRepository vehicleRepository;
    private final UserRepository userRepository;
    private final ServiceRepository serviceRepository;
    private final SparePartRepository sparePartRepository;

    @Transactional
    public Inspection createInspection(InspectionRequest request) {
        Inspection inspection = Inspection.builder()
                .vehicle(vehicleRepository.findById(request.getVehicleId())
                        .orElseThrow(() -> new ResourceNotFoundException("Không tìm thấy xe")))
                .technician(request.getTechnicianId() != null ?
                        userRepository.findById(request.getTechnicianId()).orElse(null) : null)
                .initialCondition(request.getInitialCondition())
                .diagnosticResult(request.getDiagnosticResult())
                .build();
        return inspectionRepository.save(inspection);
    }

    @Transactional
    public RepairResponse createRepairOrder(RepairRequest request) {
        RepairOrder order = RepairOrder.builder()
                .orderCode("RO-" + System.currentTimeMillis() / 1000)
                .vehicle(vehicleRepository.findById(request.getVehicleId())
                        .orElseThrow(() -> new ResourceNotFoundException("Không tìm thấy xe")))
                .customer(userRepository.findById(request.getCustomerId())
                        .orElseThrow(() -> new ResourceNotFoundException("Không tìm thấy khách hàng")))
                .status(RepairOrder.RepairStatus.RECEIVING)
                .note(request.getNote())
                .build();

        if (request.getInspectionId() != null) {
            order.setInspection(inspectionRepository.findById(request.getInspectionId()).orElse(null));
        }

        BigDecimal total = BigDecimal.ZERO;
        if (request.getItems() != null && !request.getItems().isEmpty()) {
            for (RepairRequest.RepairItemRequest itemReq : request.getItems()) {
                RepairItem item = buildRepairItem(order, itemReq);
                order.getItems().add(item);
                total = total.add(item.getTotalPrice());
            }
        }
        order.setTotalAmount(total);

        return mapToResponse(repairOrderRepository.save(order));
    }

    @Transactional
    public RepairResponse updateRepairOrder(Long id, RepairUpdateRequest request) {
        RepairOrder order = repairOrderRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Không tìm thấy đơn sửa chữa"));

        if (request.getStatus() != null) {
            order.setStatus(request.getStatus());
            if (request.getStatus() == RepairOrder.RepairStatus.COMPLETED) {
                order.setCompletedAt(LocalDateTime.now());
            }
        }

        if (request.getTechnicianId() != null) {
            order.setTechnician(userRepository.findById(request.getTechnicianId()).orElse(null));
        }

        if (request.getNote() != null) {
            order.setNote(request.getNote());
        }

        return mapToResponse(repairOrderRepository.save(order));
    }

    public Page<RepairResponse> getOrdersPaged(RepairOrder.RepairStatus status, String keyword, Pageable pageable) {
        return repairOrderRepository.searchRepairOrders(status, keyword, pageable)
                .map(this::mapToResponse);
    }

    public RepairResponse getOrderById(Long id) {
        return mapToResponse(repairOrderRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Không tìm thấy phiếu sửa chữa")));
    }

    private RepairItem buildRepairItem(RepairOrder order, RepairRequest.RepairItemRequest itemReq) {
        BigDecimal unitPrice = itemReq.getUnitPrice() != null ? itemReq.getUnitPrice() : BigDecimal.ZERO;
        BigDecimal itemTotal = unitPrice.multiply(BigDecimal.valueOf(itemReq.getQuantity()));

        RepairItem item = RepairItem.builder()
                .repairOrder(order)
                .quantity(itemReq.getQuantity())
                .unitPrice(unitPrice)
                .totalPrice(itemTotal)
                .build();

        if (itemReq.getServiceId() != null) {
            item.setService(serviceRepository.findById(itemReq.getServiceId()).orElse(null));
        }
        if (itemReq.getSparePartId() != null) {
            item.setSparePart(sparePartRepository.findById(itemReq.getSparePartId()).orElse(null));
        }

        return item;
    }

    public RepairResponse mapToResponse(RepairOrder order) {
        return RepairResponse.builder()
                .id(order.getId())
                .orderCode(order.getOrderCode())
                .vehicleId(order.getVehicle().getId())
                .licensePlate(order.getVehicle().getLicensePlate())
                .vehicleModel(order.getVehicle().getModel())
                .customerId(order.getCustomer().getId())
                .customerName(order.getCustomer().getFullName())
                .customerPhone(order.getCustomer().getPhone())
                .technicianName(order.getTechnician() != null ? order.getTechnician().getFullName() : "Chưa phân công")
                .status(order.getStatus())
                .totalAmount(order.getTotalAmount())
                .note(order.getNote())
                .createdAt(order.getCreatedAt())
                .completedAt(order.getCompletedAt())
                .items(order.getItems().stream().map(i -> RepairResponse.RepairItemResponse.builder()
                        .id(i.getId())
                        .itemName(i.getService() != null ? i.getService().getName() :
                                (i.getSparePart() != null ? i.getSparePart().getName() : "Khác"))
                        .type(i.getService() != null ? "DỊCH VỤ" : "PHỤ TÙNG")
                        .quantity(i.getQuantity())
                        .unitPrice(i.getUnitPrice())
                        .totalPrice(i.getTotalPrice())
                        .build()).collect(Collectors.toList()))
                .build();
    }

    public void saveDiagnosis(DiagnosisRequest request) {
        // Logic cập nhật kết quả chẩn đoán và thêm danh sách hạng mục đề xuất vào RepairOrder / Inspection
    }
}