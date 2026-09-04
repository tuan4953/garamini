package com.garage.user.controller;

import org.springframework.security.core.Authentication;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;

@Controller
@RequestMapping("/customer")
public class CustomerController {

    // Đã đổi path để tránh trùng với VehicleController
    @GetMapping("/my-vehicles")
    public String showVehicles(Authentication authentication, Model model) {
        return "customer/vehicles";
    }

    // Đã đổi path để tránh trùng với AppointmentController
    @GetMapping("/my-appointments")
    public String showAppointments(Authentication authentication, Model model) {
        return "customer/appointments";
    }

    @GetMapping("/invoices")
    public String showInvoices(Authentication authentication, Model model) {
        return "customer/invoices";
    }
}