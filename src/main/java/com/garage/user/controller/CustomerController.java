package com.garage.user.controller;

import com.garage.security.CustomUserDetails;
import com.garage.warranty.repository.WarrantyRepository; // 1. Import WarrantyRepository
import lombok.RequiredArgsConstructor; // 2. Import RequiredArgsConstructor
import org.springframework.security.core.Authentication;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;

import java.util.Collections;

@Controller
@RequestMapping("/customer")
@RequiredArgsConstructor // 3. Tự động sinh Constructor inject WarrantyRepository
public class CustomerController {

    // 4. Khai báo field warrantyRepository
    private final WarrantyRepository warrantyRepository;

    @GetMapping("/my-vehicles")
    public String showVehicles(Authentication authentication, Model model) {
        return "customer/vehicles";
    }

    @GetMapping("/my-appointments")
    public String showAppointments(Authentication authentication, Model model) {
        return "customer/appointments";
    }

    @GetMapping("/invoices")
    public String showInvoices(Authentication authentication, Model model) {
        return "customer/invoices";
    }

    @GetMapping("/warranty")
    public String showWarrantyPage(@AuthenticationPrincipal CustomUserDetails userDetails, Model model) {
        if (userDetails == null) {
            return "redirect:/login";
        }

        // Lấy danh sách bảo hành của khách hàng từ DB
        model.addAttribute("warranties", warrantyRepository.findByCustomerId(userDetails.getId()));
        return "customer/warranty";
    }

    @GetMapping("/repair-progress")
    public String showRepairProgress(@AuthenticationPrincipal CustomUserDetails userDetails, Model model) {
        if (userDetails == null) {
            return "redirect:/login";
        }

        model.addAttribute("repairOrders", Collections.emptyList());
        return "customer/repair-progress";
    }
}