package com.garage.user.controller;

import com.garage.security.CustomUserDetails;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;

import java.util.Collections;

@Controller
@RequestMapping("/customer")
public class CustomerController {

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
        // Kiểm tra an toàn tránh lỗi NullPointer nếu người dùng chưa đăng nhập
        if (userDetails == null) {
            return "redirect:/login";
        }

        model.addAttribute("warranties", Collections.emptyList());
        return "customer/warranty";
    }

    // ✅ Đã sửa từ "/customer/repair-progress" thành "/repair-progress"
    @GetMapping("/repair-progress")
    public String showRepairProgress(@AuthenticationPrincipal CustomUserDetails userDetails, Model model) {
        if (userDetails == null) {
            return "redirect:/login";
        }

        model.addAttribute("repairOrders", Collections.emptyList());
        return "customer/repair-progress";
    }
}