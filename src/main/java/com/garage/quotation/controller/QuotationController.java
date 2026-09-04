package com.garage.quotation.controller;

import com.garage.quotation.dto.QuotationResponse;
import com.garage.quotation.service.QuotationService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/quotations")
@RequiredArgsConstructor
public class QuotationController {

    private final QuotationService quotationService;

    @GetMapping("/{id}")
    public ResponseEntity<QuotationResponse> getQuotation(@PathVariable Long id) {
        return ResponseEntity.ok(quotationService.getQuotationById(id));
    }
}