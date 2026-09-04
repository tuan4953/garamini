package com.garage.vehicle.controller;

import com.garage.vehicle.dto.VehicleRequest;
import com.garage.vehicle.dto.VehicleResponse;
import com.garage.vehicle.service.VehicleService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;
@Controller
@RequestMapping("/admin/vehicles")
@RequiredArgsConstructor
public class VehicleManagementController {

    private final VehicleService vehicleService;

    @GetMapping
    public String listAllVehicles(@RequestParam(required = false) String keyword,
                                  @RequestParam(defaultValue = "0") int page,
                                  @RequestParam(defaultValue = "10") int size,
                                  Model model) {
        Page<VehicleResponse> vehiclePage = vehicleService.getAllVehiclesPaged(keyword, PageRequest.of(page, size));
        model.addAttribute("vehiclePage", vehiclePage);
        model.addAttribute("keyword", keyword);
        return "admin/vehicles";
    }

    @GetMapping("/edit/{id}")
    public String showEditForm(@PathVariable Long id, Model model) {
        VehicleResponse response = vehicleService.getVehicleById(id);
        VehicleRequest request = VehicleRequest.builder()
                .id(response.getId())
                .licensePlate(response.getLicensePlate())
                .brand(response.getBrand())
                .model(response.getModel())
                .color(response.getColor())
                .manufactureYear(response.getManufactureYear())
                .chassisNumber(response.getChassisNumber())
                .engineNumber(response.getEngineNumber())
                .imageUrl(response.getImageUrl())
                .ownerId(response.getOwnerId())
                .build();
        model.addAttribute("vehicleRequest", request);
        return "vehicle/form";
    }

    @PostMapping("/edit/{id}")
    public String updateVehicle(@PathVariable Long id,
                                @Valid @ModelAttribute("vehicleRequest") VehicleRequest request,
                                BindingResult bindingResult,
                                RedirectAttributes redirectAttributes) {
        if (bindingResult.hasErrors()) {
            return "vehicle/form";
        }
        vehicleService.updateVehicle(id, request);
        redirectAttributes.addFlashAttribute("successMessage", "Cập nhật thông tin xe thành công!");
        return "redirect:/admin/vehicles";
    }

    @PostMapping("/delete/{id}")
    public String deleteVehicle(@PathVariable Long id, RedirectAttributes redirectAttributes) {
        vehicleService.deleteVehicle(id);
        redirectAttributes.addFlashAttribute("successMessage", "Xóa xe thành công!");
        return "redirect:/admin/vehicles";
    }
}
