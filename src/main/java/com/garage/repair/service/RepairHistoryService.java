package com.garage.repair.service;

import com.garage.repair.dto.AssignedJobResponse;
import com.garage.repair.dto.RepairResponse;
import com.garage.repair.repository.RepairOrderRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class RepairHistoryService {

    private final RepairOrderRepository repairOrderRepository;
    private final RepairService repairService;

    public List<RepairResponse> getCustomerRepairHistory(Long customerId) {
        return repairOrderRepository.findByCustomerIdOrderByCreatedAtDesc(customerId)
                .stream().map(repairService::mapToResponse).collect(Collectors.toList());
    }

    public List<RepairResponse> getVehicleRepairHistory(Long vehicleId) {
        return repairOrderRepository.findByVehicleIdOrderByCreatedAtDesc(vehicleId)
                .stream().map(repairService::mapToResponse).collect(Collectors.toList());
    }

    // THÊM HÀM NÀY: Lấy danh sách công việc của KTV
    public List<AssignedJobResponse> getAssignedJobsByTechnicianEmail(String email) {
        return repairOrderRepository.findByTechnicianEmailOrderByCreatedAtDesc(email)
                .stream()
                .map(order -> AssignedJobResponse.builder()
                        .orderId(order.getId())
                        .orderCode(order.getOrderCode())
                        .licensePlate(order.getVehicle() != null ? order.getVehicle().getLicensePlate() : "N/A")
                        .vehicleModel(order.getVehicle() != null ? order.getVehicle().getModel() : "N/A")
                        .customerName(order.getCustomer() != null ? order.getCustomer().getFullName() : "Khách lẻ")
                        .customerPhone(order.getCustomer() != null ? order.getCustomer().getPhone() : "")
                        .description(order.getNote())
                        .status(order.getStatus())
                        .createdAt(order.getCreatedAt())
                        .build())
                .collect(Collectors.toList());
    }
}