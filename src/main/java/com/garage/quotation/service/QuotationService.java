package com.garage.quotation.service;

import com.garage.exception.ResourceNotFoundException;
import com.garage.quotation.dto.*;
import com.garage.quotation.model.*;
import com.garage.quotation.repository.*;
import com.garage.service.repository.ServiceRepository;
import com.garage.sparepart.repository.SparePartRepository;
import com.garage.user.repository.UserRepository;
import com.garage.vehicle.repository.VehicleRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;

import java.math.BigDecimal;
import java.util.stream.Collectors;

@org.springframework.stereotype.Service
@RequiredArgsConstructor
public class QuotationService {

    private final QuotationRepository quotationRepository;
    private final VehicleRepository vehicleRepository;
    private final UserRepository userRepository;
    private final ServiceRepository serviceRepository;
    private final SparePartRepository sparePartRepository;

    public Page<QuotationResponse> searchQuotations(Quotation.QuotationStatus status, String keyword, Pageable pageable) {
        return quotationRepository.searchQuotations(status, keyword, pageable).map(this::mapToResponse);
    }

    public QuotationResponse getQuotationById(Long id) {
        return mapToResponse(quotationRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Không tìm thấy báo giá")));
    }

    @org.springframework.transaction.annotation.Transactional
    public QuotationResponse createQuotation(QuotationRequest request) {
        Quotation quotation = Quotation.builder()
                .quotationCode("QT-" + System.currentTimeMillis() / 1000)
                .vehicle(vehicleRepository.findById(request.getVehicleId())
                        .orElseThrow(() -> new ResourceNotFoundException("Không tìm thấy xe")))
                .customer(userRepository.findById(request.getCustomerId())
                        .orElseThrow(() -> new ResourceNotFoundException("Không tìm thấy khách hàng")))
                .status(Quotation.QuotationStatus.PENDING)
                .note(request.getNote())
                .build();

        BigDecimal total = BigDecimal.ZERO;
        if (request.getItems() != null && !request.getItems().isEmpty()) {
            for (QuotationItemRequest itemReq : request.getItems()) {
                QuotationItem item = buildQuotationItem(quotation, itemReq);
                quotation.getItems().add(item);
                total = total.add(item.getTotalPrice());
            }
        }
        quotation.setTotalAmount(total);
        return mapToResponse(quotationRepository.save(quotation));
    }

    @org.springframework.transaction.annotation.Transactional
    public void updateStatus(Long id, Quotation.QuotationStatus status) {
        Quotation quotation = quotationRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Không tìm thấy báo giá"));
        quotation.setStatus(status);
        quotationRepository.save(quotation);
    }

    private QuotationItem buildQuotationItem(Quotation quotation, QuotationItemRequest itemReq) {
        BigDecimal unitPrice = itemReq.getUnitPrice() != null ? itemReq.getUnitPrice() : BigDecimal.ZERO;
        BigDecimal itemTotal = unitPrice.multiply(BigDecimal.valueOf(itemReq.getQuantity()));

        QuotationItem item = QuotationItem.builder()
                .quotation(quotation)
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

    public QuotationResponse mapToResponse(Quotation q) {
        return QuotationResponse.builder()
                .id(q.getId())
                .quotationCode(q.getQuotationCode())
                .vehicleId(q.getVehicle().getId())
                .licensePlate(q.getVehicle().getLicensePlate())
                .vehicleModel(q.getVehicle().getModel())
                .customerId(q.getCustomer().getId())
                .customerName(q.getCustomer().getFullName())
                .customerPhone(q.getCustomer().getPhone())
                .status(q.getStatus())
                .totalAmount(q.getTotalAmount())
                .note(q.getNote())
                .createdAt(q.getCreatedAt())
                .items(q.getItems().stream().map(i -> QuotationResponse.QuotationItemResponse.builder()
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
}