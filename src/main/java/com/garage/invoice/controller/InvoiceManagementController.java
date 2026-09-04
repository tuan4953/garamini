package com.garage.invoice.controller;

import com.garage.invoice.dto.InvoiceRequest;
import com.garage.invoice.model.Invoice.PaymentStatus;
import com.garage.invoice.service.InvoiceService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Sort;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

@Controller
@RequestMapping("/admin/invoices")
@RequiredArgsConstructor
public class InvoiceManagementController {

    private final InvoiceService invoiceService;

    @GetMapping
    public String listInvoices(@RequestParam(required = false) PaymentStatus status,
                               @RequestParam(required = false) String keyword,
                               @RequestParam(defaultValue = "0") int page,
                               @RequestParam(defaultValue = "10") int size,
                               Model model) {
        model.addAttribute("invoicePage", invoiceService.searchInvoices(
                status, keyword, PageRequest.of(page, size, Sort.by("issuedAt").descending())));
        model.addAttribute("statuses", PaymentStatus.values());
        model.addAttribute("selectedStatus", status);
        model.addAttribute("keyword", keyword);
        return "admin/invoice-list";
    }

    @GetMapping("/create")
    public String showCreateForm(Model model) {
        model.addAttribute("invoiceRequest", new InvoiceRequest());
        return "admin/invoice-form";
    }

    @PostMapping("/create")
    public String createInvoice(@Valid @ModelAttribute("invoiceRequest") InvoiceRequest request,
                                BindingResult result,
                                RedirectAttributes ra) {
        if (result.hasErrors()) {
            return "admin/invoice-form";
        }
        invoiceService.createInvoice(request);
        ra.addFlashAttribute("successMessage", "Tạo hóa đơn mới thành công!");
        return "redirect:/admin/invoices";
    }

    @GetMapping("/{id}")
    public String viewDetail(@PathVariable Long id, Model model) {
        model.addAttribute("invoice", invoiceService.getInvoiceById(id));
        return "admin/invoice-detail";
    }

    @PostMapping("/{id}/pay")
    public String payInvoice(@PathVariable Long id,
                             @RequestParam String paymentMethod,
                             RedirectAttributes ra) {
        invoiceService.payInvoice(id, paymentMethod);
        ra.addFlashAttribute("successMessage", "Xác nhận thanh toán thành công!");
        return "redirect:/admin/invoices/" + id;
    }
}