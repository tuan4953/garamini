package com.garage.rescue.controller;

import com.garage.rescue.dto.RescueUpdateDto;
import com.garage.rescue.model.RescueRequest.RescueStatus;
import com.garage.rescue.service.RescueService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Sort;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

@Controller
@RequestMapping("/admin/rescue")
@RequiredArgsConstructor
@PreAuthorize("hasAnyRole('ADMIN', 'MANAGER')") // Bảo vệ đường dẫn Admin
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
        model.addAttribute("keyword", keyword);
        return "admin/rescue-list";
    }

    @GetMapping("/{id}")
    public String viewDetail(@PathVariable Long id, Model model) {
        model.addAttribute("rescue", rescueService.getRequestById(id));
        model.addAttribute("statuses", RescueStatus.values());
        return "admin/rescue-detail";
    }

    @PostMapping("/{id}/update")
    public String updateStatus(@PathVariable Long id,
                               @Valid @ModelAttribute RescueUpdateDto updateDto,
                               RedirectAttributes ra) {
        rescueService.updateRescueRequest(id, updateDto);
        ra.addFlashAttribute("successMessage", "Cập nhật thông tin cứu hộ thành công!");
        return "redirect:/admin/rescue/" + id;
    }
}