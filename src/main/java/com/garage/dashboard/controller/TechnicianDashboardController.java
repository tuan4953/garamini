package com.garage.dashboard.controller;

import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.security.core.Authentication;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;

@Controller
@RequestMapping("/technician")
@PreAuthorize("hasAnyRole('TECHNICIAN', 'ADMIN')") // Bảo vệ đường dẫn chỉ cho Kỹ thuật viên & Admin
public class TechnicianDashboardController {

    @GetMapping({"", "/", "/dashboard"})
    public String showTechnicianDashboard(Authentication authentication, Model model) {
        if (authentication != null) {
            model.addAttribute("username", authentication.getName());
        }

        // Trỏ trực tiếp tới giao diện Bàn làm việc Kỹ thuật viên (templates/technician/dashboard.html)
        return "technician/dashboard";
    }
}