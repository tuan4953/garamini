package com.garage.vehicle.controller;

import org.springframework.ui.Model;
import com.garage.security.CustomUserDetails;
import com.garage.vehicle.dto.VehicleRequest;
import com.garage.vehicle.dto.VehicleResponse;
import com.garage.vehicle.service.VehicleService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.stereotype.Controller;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

import java.util.List;

@Controller
@RequestMapping("/customer/vehicles")
@RequiredArgsConstructor
public class VehicleController {

    private final VehicleService vehicleService;

    @GetMapping
    public String listCustomerVehicles(@AuthenticationPrincipal CustomUserDetails userDetails, Model model) {
        List<VehicleResponse> vehicles = vehicleService.getVehiclesByOwner(userDetails.getId());
        model.addAttribute("vehicles", vehicles);

        // Thêm object này để Modal trong vehicles.html bind được dữ liệu
        VehicleRequest request = new VehicleRequest();
        request.setOwnerId(userDetails.getId());
        model.addAttribute("vehicleRequest", request);

        return "customer/vehicles";
    }

    @GetMapping("/{id}")
    public String vehicleDetail(@PathVariable Long id, Model model) {
        VehicleResponse vehicle = vehicleService.getVehicleById(id);
        model.addAttribute("vehicle", vehicle);
        return "vehicle/detail";
    }

    @GetMapping("/add")
    public String showAddForm(Model model, @AuthenticationPrincipal CustomUserDetails userDetails) {
        VehicleRequest request = new VehicleRequest();
        request.setOwnerId(userDetails.getId());
        model.addAttribute("vehicleRequest", request);
        return "vehicle/form";
    }

    @PostMapping("/add")
    public String createVehicle(@Valid @ModelAttribute("vehicleRequest") VehicleRequest request,
                                BindingResult bindingResult,
                                @AuthenticationPrincipal CustomUserDetails userDetails,
                                RedirectAttributes redirectAttributes) {
        if (bindingResult.hasErrors()) {
            return "vehicle/form";
        }
        request.setOwnerId(userDetails.getId());
        vehicleService.createVehicle(request);
        redirectAttributes.addFlashAttribute("successMessage", "Thêm xe thành công!");
        return "redirect:/customer/vehicles";
    }
}
