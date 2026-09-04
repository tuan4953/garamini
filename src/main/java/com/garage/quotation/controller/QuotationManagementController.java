package com.garage.quotation.controller;

import com.garage.quotation.dto.QuotationRequest;
import com.garage.quotation.model.Quotation;
import com.garage.quotation.service.QuotationService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.PageRequest;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

@Controller
@RequestMapping("/admin/quotations")
@RequiredArgsConstructor
public class QuotationManagementController {

    private final QuotationService quotationService;

    @GetMapping
    public String listQuotations(@RequestParam(required = false) Quotation.QuotationStatus status,
                                 @RequestParam(required = false) String keyword,
                                 @RequestParam(defaultValue = "0") int page,
                                 @RequestParam(defaultValue = "10") int size,
                                 Model model) {
        model.addAttribute("quotationsPage", quotationService.searchQuotations(status, keyword, PageRequest.of(page, size)));
        model.addAttribute("statuses", Quotation.QuotationStatus.values());
        model.addAttribute("selectedStatus", status);
        model.addAttribute("keyword", keyword);
        return "admin/quotations-list";
    }

    @GetMapping("/create")
    public String showCreateForm(Model model) {
        model.addAttribute("quotationRequest", new QuotationRequest());
        return "admin/quotation-form";
    }

    @PostMapping("/create")
    public String createQuotation(@Valid @ModelAttribute("quotationRequest") QuotationRequest request,
                                  BindingResult result,
                                  RedirectAttributes ra) {
        if (result.hasErrors()) {
            return "admin/quotation-form";
        }
        quotationService.createQuotation(request);
        ra.addFlashAttribute("successMessage", "Tạo báo giá thành công!");
        return "redirect:/admin/quotations";
    }

    @GetMapping("/{id}")
    public String viewDetail(@PathVariable Long id, Model model) {
        model.addAttribute("quotation", quotationService.getQuotationById(id));
        return "admin/quotation-detail";
    }

    @PostMapping("/{id}/status")
    public String updateStatus(@PathVariable Long id, @RequestParam Quotation.QuotationStatus status, RedirectAttributes ra) {
        quotationService.updateStatus(id, status);
        ra.addFlashAttribute("successMessage", "Cập nhật trạng thái báo giá thành công!");
        return "redirect:/admin/quotations/" + id;
    }
}