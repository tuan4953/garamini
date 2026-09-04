package com.garage.repair.controller;

import com.garage.repair.dto.*;
import com.garage.repair.model.RepairOrder.RepairStatus;
import com.garage.repair.service.RepairService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Sort;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

@Controller
@RequestMapping("/admin/repairs")
@RequiredArgsConstructor
public class RepairManagementController {

    private final RepairService repairService;

    @GetMapping
    public String listOrders(@RequestParam(required = false) RepairStatus status,
                             @RequestParam(required = false) String keyword,
                             @RequestParam(defaultValue = "0") int page,
                             @RequestParam(defaultValue = "10") int size,
                             Model model) {
        model.addAttribute("ordersPage", repairService.getOrdersPaged(
                status, keyword, PageRequest.of(page, size, Sort.by("createdAt").descending())));
        model.addAttribute("statuses", RepairStatus.values());
        model.addAttribute("selectedStatus", status);
        model.addAttribute("keyword", keyword);
        return "admin/repairs-list";
    }

    @GetMapping("/create")
    public String showCreateForm(Model model) {
        model.addAttribute("repairRequest", new RepairRequest());
        return "admin/repair-form";
    }

    @PostMapping("/create")
    public String createOrder(@Valid @ModelAttribute RepairRequest request, RedirectAttributes ra) {
        repairService.createRepairOrder(request);
        ra.addFlashAttribute("successMessage", "Tạo phiếu sửa chữa thành công!");
        return "redirect:/admin/repairs";
    }

    @PostMapping("/{id}/update-status")
    public String updateStatus(@PathVariable Long id,
                               @RequestParam RepairStatus status,
                               RedirectAttributes ra) {
        repairService.updateRepairOrder(id, RepairUpdateRequest.builder().status(status).build());
        ra.addFlashAttribute("successMessage", "Đã cập nhật trạng thái đơn!");
        return "redirect:/admin/repairs";
    }
}