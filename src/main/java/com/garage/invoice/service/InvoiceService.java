package com.garage.invoice.service;

import com.garage.exception.ResourceNotFoundException;
import com.garage.invoice.dto.InvoiceRequest;
import com.garage.invoice.dto.InvoiceResponse;
import com.garage.invoice.model.Invoice;
import com.garage.invoice.model.Invoice.PaymentStatus;
import com.garage.invoice.repository.InvoiceRepository;
import com.garage.user.model.User;
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

@Service
@RequiredArgsConstructor
public class InvoiceService {

    private final InvoiceRepository invoiceRepository;
    private final UserRepository userRepository;
    private final VehicleRepository vehicleRepository;

    public Page<InvoiceResponse> searchInvoices(PaymentStatus status, String keyword, Pageable pageable) {
        return invoiceRepository.searchInvoices(status, keyword, pageable).map(this::mapToResponse);
    }

    public InvoiceResponse getInvoiceById(Long id) {
        return mapToResponse(invoiceRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Không tìm thấy hóa đơn ID: " + id)));
    }

    @Transactional
    public InvoiceResponse createInvoice(InvoiceRequest request) {
        User customer = userRepository.findById(request.getCustomerId())
                .orElseThrow(() -> new ResourceNotFoundException("Không tìm thấy khách hàng"));

        BigDecimal amount = request.getAmount();
        BigDecimal discount = request.getDiscount() != null ? request.getDiscount() : BigDecimal.ZERO;
        BigDecimal totalAmount = amount.subtract(discount);

        Invoice invoice = Invoice.builder()
                .invoiceNumber("INV-" + UUID.randomUUID().toString().substring(0, 8).toUpperCase())
                .customer(customer)
                .vehicle(request.getVehicleId() != null ? vehicleRepository.findById(request.getVehicleId()).orElse(null) : null)
                .repairOrderId(request.getRepairOrderId())
                .amount(amount)
                .discount(discount)
                .totalAmount(totalAmount.compareTo(BigDecimal.ZERO) < 0 ? BigDecimal.ZERO : totalAmount)
                .paymentStatus(PaymentStatus.UNPAID)
                .paymentMethod(request.getPaymentMethod())
                .note(request.getNote())
                .build();

        return mapToResponse(invoiceRepository.save(invoice));
    }

    @Transactional
    public InvoiceResponse payInvoice(Long id, String paymentMethod) {
        Invoice invoice = invoiceRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Không tìm thấy hóa đơn ID: " + id));

        invoice.setPaymentStatus(PaymentStatus.PAID);
        invoice.setPaymentMethod(paymentMethod);
        invoice.setPaidAt(LocalDateTime.now());

        return mapToResponse(invoiceRepository.save(invoice));
    }

    private InvoiceResponse mapToResponse(Invoice inv) {
        return InvoiceResponse.builder()
                .id(inv.getId())
                .invoiceNumber(inv.getInvoiceNumber())
                .customerId(inv.getCustomer().getId())
                .customerName(inv.getCustomer().getFullName())
                .customerPhone(inv.getCustomer().getPhone())
                .vehicleId(inv.getVehicle() != null ? inv.getVehicle().getId() : null)
                .licensePlate(inv.getVehicle() != null ? inv.getVehicle().getLicensePlate() : "N/A")
                .repairOrderId(inv.getRepairOrderId())
                .amount(inv.getAmount())
                .discount(inv.getDiscount())
                .totalAmount(inv.getTotalAmount())
                .paymentStatus(inv.getPaymentStatus())
                .paymentMethod(inv.getPaymentMethod())
                .note(inv.getNote())
                .issuedAt(inv.getIssuedAt())
                .paidAt(inv.getPaidAt())
                .build();
    }
}