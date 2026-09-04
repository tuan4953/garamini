package com.garage.service.controller;


import com.garage.service.dto.ServiceRequest;
import com.garage.service.dto.ServiceResponse;
import com.garage.service.service.ServiceManagementService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Sort;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

@Controller
@RequestMapping("/admin/services")
@RequiredArgsConstructor
public class ServiceManagementController {

    private final ServiceManagementService serviceManagementService;

    @GetMapping
    public String listAllServices(@RequestParam(required = false) Boolean active,
                                  @RequestParam(required = false) String keyword,
                                  @RequestParam(defaultValue = "0") int page,
                                  @RequestParam(defaultValue = "10") int size,
                                  Model model) {
        Page<ServiceResponse> servicePage = serviceManagementService.getAllServicesPaged(
                active, keyword, PageRequest.of(page, size, Sort.by("name").ascending()));

        model.addAttribute("servicePage", servicePage);
        model.addAttribute("selectedActive", active);
        model.addAttribute("keyword", keyword);
        return "admin/services";
    }

    @GetMapping("/create")
    public String showCreateForm(Model model) {
        model.addAttribute("serviceRequest", new ServiceRequest());
        return "admin/service-form";
    }

    @PostMapping("/create")
    public String createService(@Valid @ModelAttribute("serviceRequest") ServiceRequest request,
                                BindingResult bindingResult,
                                RedirectAttributes redirectAttributes) {
        if (bindingResult.hasErrors()) {
            return "admin/service-form";
        }

        serviceManagementService.createService(request);
        redirectAttributes.addFlashAttribute("successMessage", "Thêm dịch vụ mới thành công!");
        return "redirect:/admin/services";
    }

    @GetMapping("/edit/{id}")
    public String showEditForm(@PathVariable Long id, Model model) {
        ServiceResponse response = serviceManagementService.getServiceById(id);
        ServiceRequest request = ServiceRequest.builder()
                .id(response.getId())
                .name(response.getName())
                .description(response.getDescription())
                .price(response.getPrice())
                .estimatedDurationMinutes(response.getEstimatedDurationMinutes())
                .active(response.getActive())
                .build();

        model.addAttribute("serviceRequest", request);
        return "admin/service-form";
    }

    @PostMapping("/edit/{id}")
    public String updateService(@PathVariable Long id,
                                @Valid @ModelAttribute("serviceRequest") ServiceRequest request,
                                BindingResult bindingResult,
                                RedirectAttributes redirectAttributes) {
        if (bindingResult.hasErrors()) {
            return "admin/service-form";
        }

        serviceManagementService.updateService(id, request);
        redirectAttributes.addFlashAttribute("successMessage", "Cập nhật dịch vụ thành công!");
        return "redirect:/admin/services";
    }

    @PostMapping("/{id}/toggle-status")
    public String toggleStatus(@PathVariable Long id, RedirectAttributes redirectAttributes) {
        serviceManagementService.toggleServiceStatus(id);
        redirectAttributes.addFlashAttribute("successMessage", "Đổi trạng thái dịch vụ thành công!");
        return "redirect:/admin/services";
    }
}