package com.garage.sparepart.service;

import com.garage.exception.BusinessException;
import com.garage.exception.ResourceNotFoundException;
import com.garage.sparepart.dto.SparePartRequest;
import com.garage.sparepart.dto.SparePartResponse;
import com.garage.sparepart.model.SparePart;
import com.garage.sparepart.repository.SparePartRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.stream.Collectors;

@org.springframework.stereotype.Service
@RequiredArgsConstructor
public class SparePartService {

    private final SparePartRepository sparePartRepository;

    public List<SparePartResponse> getAllActiveSpareParts() {
        return sparePartRepository.findByActiveTrueOrderByNameAsc().stream()
                .map(this::mapToResponse)
                .collect(Collectors.toList());
    }

    public SparePartResponse getSparePartById(Long id) {
        SparePart sparePart = sparePartRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Không tìm thấy phụ tùng với ID: " + id));
        return mapToResponse(sparePart);
    }

    public Page<SparePartResponse> getAllSparePartsPaged(Boolean active, String keyword, Pageable pageable) {
        return sparePartRepository.searchSpareParts(active, keyword, pageable)
                .map(this::mapToResponse);
    }

    @Transactional
    public SparePartResponse createSparePart(SparePartRequest request) {
        if (sparePartRepository.existsByName(request.getName().trim())) {
            throw new BusinessException("Tên phụ tùng đã tồn tại trong hệ thống");
        }

        SparePart sparePart = SparePart.builder()
                .name(request.getName().trim())
                .description(request.getDescription())
                .price(request.getPrice())
                .stockQuantity(request.getStockQuantity() != null ? request.getStockQuantity() : 0)
                .unit(request.getUnit())
                .active(request.getActive() != null ? request.getActive() : true)
                .build();

        return mapToResponse(sparePartRepository.save(sparePart));
    }

    @Transactional
    public SparePartResponse updateSparePart(Long id, SparePartRequest request) {
        SparePart sparePart = sparePartRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Không tìm thấy phụ tùng với ID: " + id));

        if (sparePartRepository.existsByNameAndIdNot(request.getName().trim(), id)) {
            throw new BusinessException("Tên phụ tùng này đã trùng với một phụ tùng khác");
        }

        sparePart.setName(request.getName().trim());
        sparePart.setDescription(request.getDescription());
        sparePart.setPrice(request.getPrice());
        sparePart.setStockQuantity(request.getStockQuantity());
        sparePart.setUnit(request.getUnit());
        if (request.getActive() != null) {
            sparePart.setActive(request.getActive());
        }

        return mapToResponse(sparePartRepository.save(sparePart));
    }

    @Transactional
    public void toggleSparePartStatus(Long id) {
        SparePart sparePart = sparePartRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Không tìm thấy phụ tùng để cập nhật"));
        sparePart.setActive(!sparePart.getActive());
        sparePartRepository.save(sparePart);
    }

    private SparePartResponse mapToResponse(SparePart sparePart) {
        return SparePartResponse.builder()
                .id(sparePart.getId())
                .name(sparePart.getName())
                .description(sparePart.getDescription())
                .price(sparePart.getPrice())
                .stockQuantity(sparePart.getStockQuantity())
                .unit(sparePart.getUnit())
                .active(sparePart.getActive())
                .createdAt(sparePart.getCreatedAt())
                .updatedAt(sparePart.getUpdatedAt())
                .build();
    }
}