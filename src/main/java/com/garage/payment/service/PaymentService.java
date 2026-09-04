package com.garage.payment.service;

import com.garage.exception.ResourceNotFoundException;
import com.garage.invoice.model.Invoice;
import com.garage.invoice.repository.InvoiceRepository;
import com.garage.payment.dto.PaymentRequest;
import com.garage.payment.model.Payment;
import com.garage.payment.model.Payment.PaymentStatus;
import com.garage.payment.repository.PaymentRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.UUID;

@Service
@RequiredArgsConstructor
public class PaymentService {

    private final PaymentRepository paymentRepository;
    private final InvoiceRepository invoiceRepository;

    public List<Payment> getPaymentsByInvoice(Long invoiceId) {
        return paymentRepository.findByInvoiceId(invoiceId);
    }

    @Transactional
    public Payment processPayment(PaymentRequest request) {
        Invoice invoice = invoiceRepository.findById(request.getInvoiceId())
                .orElseThrow(() -> new ResourceNotFoundException("Không tìm thấy hóa đơn ID: " + request.getInvoiceId()));

        Payment payment = Payment.builder()
                .transactionCode("TXN-" + UUID.randomUUID().toString().substring(0, 8).toUpperCase())
                .invoice(invoice)
                .amount(request.getAmount())
                .paymentMethod(request.getPaymentMethod())
                .status(PaymentStatus.SUCCESS)
                .note(request.getNote())
                .build();

        // Cập nhật trạng thái hóa đơn sang PAID
        invoice.setPaymentStatus(Invoice.PaymentStatus.PAID);
        invoice.setPaymentMethod(request.getPaymentMethod());
        invoice.setPaidAt(payment.getPaymentDate());
        invoiceRepository.save(invoice);

        return paymentRepository.save(payment);
    }
}