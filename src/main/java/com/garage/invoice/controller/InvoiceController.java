package com.garage.invoice.controller;

import com.garage.invoice.dto.InvoiceRequest;
import com.garage.invoice.dto.InvoiceResponse;
import com.garage.invoice.model.Invoice.PaymentStatus;
import com.garage.invoice.service.InvoiceService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Sort;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/invoices")
@RequiredArgsConstructor
public class InvoiceController {

    private final InvoiceService invoiceService;

    // 1. Lấy chi tiết hóa đơn
    @GetMapping("/{id}")
    public ResponseEntity<InvoiceResponse> getInvoice(@PathVariable Long id) {
        return ResponseEntity.ok(invoiceService.getInvoiceById(id));
    }

    // 2. Lấy danh sách hóa đơn (có phân trang & lọc)
    @GetMapping
    public ResponseEntity<Page<InvoiceResponse>> getAllInvoices(
            @RequestParam(required = false) PaymentStatus status,
            @RequestParam(required = false) String keyword,
            @RequestParam(defaultValue = "0") int page,
            @RequestParam(defaultValue = "10") int size) {

        Page<InvoiceResponse> invoices = invoiceService.searchInvoices(
                status, keyword, PageRequest.of(page, size, Sort.by("issuedAt").descending()));

        return ResponseEntity.ok(invoices);
    }

    // 3. Tạo hóa đơn mới
    @PostMapping
    public ResponseEntity<InvoiceResponse> createInvoice(@Valid @RequestBody InvoiceRequest request) {
        InvoiceResponse createdInvoice = invoiceService.createInvoice(request);
        return ResponseEntity.status(HttpStatus.CREATED).body(createdInvoice);
    }

    // 4. Thanh toán hóa đơn
    @PostMapping("/{id}/pay")
    public ResponseEntity<InvoiceResponse> payInvoice(
            @PathVariable Long id,
            @RequestParam String paymentMethod) {

        InvoiceResponse paidInvoice = invoiceService.payInvoice(id, paymentMethod);
        return ResponseEntity.ok(paidInvoice);
    }
}