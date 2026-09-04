package com.garage.vehicle.service;


import com.garage.exception.BusinessException;
import com.garage.exception.ResourceNotFoundException;
import com.garage.user.model.User;
import com.garage.user.repository.UserRepository;
import com.garage.vehicle.dto.VehicleRequest;
import com.garage.vehicle.dto.VehicleResponse;
import com.garage.vehicle.model.Vehicle;
import com.garage.vehicle.repository.VehicleRepository;
import org.springframework.transaction.annotation.Transactional;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable; // ✅ Đã sửa từ java.awt.print.Pageable sang Spring Data
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class VehicleService {

    private final VehicleRepository vehicleRepository;
    private final UserRepository userRepository;

    public List<VehicleResponse> getVehiclesByOwner(Long ownerId) {
        return vehicleRepository.findByOwnerId(ownerId).stream()
                .map(this::mapToResponse)
                .collect(Collectors.toList());
    }

    public VehicleResponse getVehicleById(Long id) {
        Vehicle vehicle = vehicleRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Không tìm thấy xe với ID: " + id));
        return mapToResponse(vehicle);
    }

    public Page<VehicleResponse> getAllVehiclesPaged(String keyword, Pageable pageable) {
        return vehicleRepository.searchVehicles(keyword, pageable)
                .map(this::mapToResponse);
    }

    @Transactional
    public VehicleResponse createVehicle(VehicleRequest request) {
        if (vehicleRepository.existsByLicensePlate(request.getLicensePlate())) {
            throw new BusinessException("Biển số xe đã tồn tại trong hệ thống");
        }

        User owner = userRepository.findById(request.getOwnerId())
                .orElseThrow(() -> new ResourceNotFoundException("Không tìm thấy khách hàng"));

        Vehicle vehicle = Vehicle.builder()
                .licensePlate(request.getLicensePlate().toUpperCase().trim())
                .brand(request.getBrand())
                .model(request.getModel())
                .color(request.getColor())
                .manufactureYear(request.getManufactureYear())
                .chassisNumber(request.getChassisNumber())
                .engineNumber(request.getEngineNumber())
                .imageUrl(request.getImageUrl())
                .owner(owner)
                .build();

        Vehicle savedVehicle = vehicleRepository.save(vehicle);
        return mapToResponse(savedVehicle);
    }

    @Transactional
    public VehicleResponse updateVehicle(Long id, VehicleRequest request) {
        Vehicle vehicle = vehicleRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Không tìm thấy xe với ID: " + id));

        if (vehicleRepository.existsByLicensePlateAndIdNot(request.getLicensePlate(), id)) {
            throw new BusinessException("Biển số xe đã được sử dụng bởi xe khác");
        }

        vehicle.setLicensePlate(request.getLicensePlate().toUpperCase().trim());
        vehicle.setBrand(request.getBrand());
        vehicle.setModel(request.getModel());
        vehicle.setColor(request.getColor());
        vehicle.setManufactureYear(request.getManufactureYear());
        vehicle.setChassisNumber(request.getChassisNumber());
        vehicle.setEngineNumber(request.getEngineNumber());
        if (request.getImageUrl() != null) {
            vehicle.setImageUrl(request.getImageUrl());
        }

        return mapToResponse(vehicleRepository.save(vehicle));
    }

    @Transactional
    public void deleteVehicle(Long id) {
        if (!vehicleRepository.existsById(id)) {
            throw new ResourceNotFoundException("Không tìm thấy xe để xóa");
        }
        vehicleRepository.deleteById(id);
    }

    private VehicleResponse mapToResponse(Vehicle vehicle) {
        return VehicleResponse.builder()
                .id(vehicle.getId())
                .licensePlate(vehicle.getLicensePlate())
                .brand(vehicle.getBrand())
                .model(vehicle.getModel())
                .color(vehicle.getColor())
                .manufactureYear(vehicle.getManufactureYear())
                .chassisNumber(vehicle.getChassisNumber())
                .engineNumber(vehicle.getEngineNumber())
                .imageUrl(vehicle.getImageUrl())
                .ownerId(vehicle.getOwner().getId())
                .ownerFullName(vehicle.getOwner().getFullName())
                .ownerPhone(vehicle.getOwner().getPhone())
                .createdAt(vehicle.getCreatedAt())
                .updatedAt(vehicle.getUpdatedAt())
                .build();
    }
}