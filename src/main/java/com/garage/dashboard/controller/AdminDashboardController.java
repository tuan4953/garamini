package com.garage.dashboard.controller;

import com.garage.dashboard.service.DashboardService;
import lombok.RequiredArgsConstructor;
import org.springframework.security.core.Authentication;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;

@Controller
@RequestMapping("/admin")
@RequiredArgsConstructor // Tự động inject DashboardService
public class AdminDashboardController {

    private final DashboardService dashboardService;

    @GetMapping("/dashboard")
    public String showAdminDashboard(Authentication authentication, Model model) {
        if (authentication != null) {
            model.addAttribute("username", authentication.getName());
        }

        // Lấy dữ liệu từ Service và truyền vào key "stats" cho Thymeleaf
        model.addAttribute("stats", dashboardService.getAdminStats());

        return "admin/dashboard";
    }
}