package com.garage.service.service;


import com.garage.exception.BusinessException;
import com.garage.exception.ResourceNotFoundException;
import com.garage.service.dto.ServiceRequest;
import com.garage.service.dto.ServiceResponse;
import com.garage.service.model.Service; // 👈 CHÚ Ý: Import đúng Entity model
import com.garage.service.repository.ServiceRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;

import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.stream.Collectors;

@org.springframework.stereotype.Service // 👈 Thay bằng viết full path như thế này để không bị trùng tên
@RequiredArgsConstructor
public class ServiceManagementService {

    private final ServiceRepository serviceRepository;

    public List<ServiceResponse> getAllActiveServices() {
        return serviceRepository.findByActiveTrueOrderByNameAsc().stream()
                .map(this::mapToResponse)
                .collect(Collectors.toList());
    }

    public ServiceResponse getServiceById(Long id) {
        com.garage.service.model.Service service = serviceRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Không tìm thấy dịch vụ với ID: " + id));
        return mapToResponse(service);
    }

    public Page<ServiceResponse> getAllServicesPaged(Boolean active, String keyword, Pageable pageable) {
        return serviceRepository.searchServices(active, keyword, pageable)
                .map(this::mapToResponse);
    }

    @Transactional
    public ServiceResponse createService(ServiceRequest request) {
        if (serviceRepository.existsByName(request.getName().trim())) {
            throw new BusinessException("Tên dịch vụ đã tồn tại trong hệ thống");
        }

        com.garage.service.model.Service service = com.garage.service.model.Service.builder()
                .name(request.getName().trim())
                .description(request.getDescription())
                .price(request.getPrice())
                .estimatedDurationMinutes(request.getEstimatedDurationMinutes())
                .active(request.getActive() != null ? request.getActive() : true)
                .build();

        return mapToResponse(serviceRepository.save(service));
    }

    @Transactional
    public ServiceResponse updateService(Long id, ServiceRequest request) {
        com.garage.service.model.Service service = serviceRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Không tìm thấy dịch vụ với ID: " + id));

        if (serviceRepository.existsByNameAndIdNot(request.getName().trim(), id)) {
            throw new BusinessException("Tên dịch vụ này đã trùng với một dịch vụ khác");
        }

        service.setName(request.getName().trim());
        service.setDescription(request.getDescription());
        service.setPrice(request.getPrice());
        service.setEstimatedDurationMinutes(request.getEstimatedDurationMinutes());
        if (request.getActive() != null) {
            service.setActive(request.getActive());
        }

        return mapToResponse(serviceRepository.save(service));
    }

    @Transactional
    public void toggleServiceStatus(Long id) {
        com.garage.service.model.Service service = serviceRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Không tìm thấy dịch vụ để cập nhật"));
        service.setActive(!service.getActive());
        serviceRepository.save(service);
    }

    private ServiceResponse mapToResponse(com.garage.service.model.Service service) {
        return ServiceResponse.builder()
                .id(service.getId())
                .name(service.getName())
                .description(service.getDescription())
                .price(service.getPrice())
                .estimatedDurationMinutes(service.getEstimatedDurationMinutes())
                .active(service.getActive())
                .createdAt(service.getCreatedAt())
                .updatedAt(service.getUpdatedAt())
                .build();
    }
}
