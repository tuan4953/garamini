package com.garage.rescue.controller;

import com.garage.rescue.dto.RescueUpdateDto;
import com.garage.rescue.model.RescueRequest.RescueStatus;
import com.garage.rescue.service.RescueService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Sort;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

@Controller
@RequestMapping("/admin/rescue")
@RequiredArgsConstructor
public class RescueManagementController {

    private final RescueService rescueService;

    @GetMapping
    public String listRequests(@RequestParam(required = false) RescueStatus status,
                               @RequestParam(required = false) String keyword,
                               @RequestParam(defaultValue = "0") int page,
                               @RequestParam(defaultValue = "10") int size,
                               Model model) {
        model.addAttribute("rescuePage", rescueService.searchRequests(
                status, keyword, PageRequest.of(page, size, Sort.by("createdAt").descending())));
        model.addAttribute("statuses", RescueStatus.values());
        model.addAttribute("selectedStatus", status);
        model.addAttribute("keyword", keyword != null ? keyword : "");
        return "admin/rescue-list";
    }

    @GetMapping("/{id}")
    public String viewDetail(@PathVariable Long id, Model model, RedirectAttributes ra) {
        try {
            model.addAttribute("rescue", rescueService.getRequestById(id));
            model.addAttribute("statuses", RescueStatus.values());
            return "admin/rescue-detail";
        } catch (Exception e) {
            ra.addFlashAttribute("errorMessage", "Không tìm thấy yêu cầu cứu hộ: " + e.getMessage());
            return "redirect:/admin/rescue";
        }
    }

    @PostMapping("/{id}/update")
    public String updateStatus(@PathVariable Long id,
                               @Valid @ModelAttribute RescueUpdateDto updateDto,
                               RedirectAttributes ra) {
        try {
            rescueService.updateRescueRequest(id, updateDto);
            ra.addFlashAttribute("successMessage", "Cập nhật thông tin cứu hộ thành công!");
        } catch (Exception e) {
            ra.addFlashAttribute("errorMessage", "Lỗi khi cập nhật cứu hộ: " + e.getMessage());
        }
        return "redirect:/admin/rescue/" + id;
    }
}