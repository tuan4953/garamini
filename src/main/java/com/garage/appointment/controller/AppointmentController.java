package com.garage.appointment.controller;

import com.garage.appointment.dto.AppointmentRequest;
import com.garage.appointment.dto.AppointmentResponse;
import com.garage.appointment.service.AppointmentService;
import com.garage.security.CustomUserDetails;
import com.garage.vehicle.dto.VehicleResponse;
import com.garage.vehicle.service.VehicleService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

import java.util.Collections;
import java.util.List;

@Controller
@RequiredArgsConstructor
public class AppointmentController {

    private final AppointmentService appointmentService;
    private final VehicleService vehicleService;

    // 1. DÙNG CHO NAVBAR TRANG CHỦ
    @GetMapping("/appointments")
    public String showPublicAppointmentForm(@AuthenticationPrincipal CustomUserDetails userDetails, Model model) {
        AppointmentRequest request = new AppointmentRequest();
        List<VehicleResponse> vehicles = Collections.emptyList();

        if (userDetails != null) {
            vehicles = vehicleService.getVehiclesByOwner(userDetails.getId());
        }

        model.addAttribute("appointmentRequest", request);
        model.addAttribute("vehicles", vehicles);
        return "customer/appointments"; // Đổi chỉ sang templates/customer/appointments.html
    }

    // 2. DANH SÁCH LỊCH HẸN CỦA KHÁCH HÀNG
    @GetMapping("/customer/appointments")
    public String listCustomerAppointments(@RequestParam(required = false) Long vehicleId,
                                           @AuthenticationPrincipal CustomUserDetails userDetails,
                                           Model model) {
        // Lấy danh sách lịch hẹn
        List<AppointmentResponse> appointments = appointmentService.getAppointmentsByCustomer(userDetails.getId());
        model.addAttribute("appointments", appointments);

        // Lấy danh sách xe để truyền vào Modal
        List<VehicleResponse> vehicles = vehicleService.getVehiclesByOwner(userDetails.getId());
        model.addAttribute("vehicles", vehicles);

        // Chuẩn bị DTO cho Modal
        AppointmentRequest request = new AppointmentRequest();
        if (vehicleId != null) {
            request.setVehicleId(vehicleId);
        }
        model.addAttribute("appointmentRequest", request);

        return "customer/appointments"; // Đổi từ "appointment/list" thành "customer/appointments"
    }

    // 3. XỬ LÝ SUBMIT FORM ĐẶT LỊCH (TẠO MỚI)
    @PostMapping("/customer/appointments/create")
    public String createAppointment(@Valid @ModelAttribute("appointmentRequest") AppointmentRequest request,
                                    BindingResult bindingResult,
                                    @AuthenticationPrincipal CustomUserDetails userDetails,
                                    Model model,
                                    RedirectAttributes redirectAttributes) {
        if (bindingResult.hasErrors()) {
            model.addAttribute("appointments", appointmentService.getAppointmentsByCustomer(userDetails.getId()));
            model.addAttribute("vehicles", vehicleService.getVehiclesByOwner(userDetails.getId()));
            return "customer/appointments";
        }

        appointmentService.createAppointment(userDetails.getId(), request);
        redirectAttributes.addFlashAttribute("successMessage", "Đặt lịch hẹn thành công!");
        return "redirect:/customer/appointments";
    }

    // 4. HỦY LỊCH HẸN
    @PostMapping("/customer/appointments/{id}/cancel")
    public String cancelAppointment(@PathVariable Long id,
                                    @RequestParam String reason,
                                    @AuthenticationPrincipal CustomUserDetails userDetails,
                                    RedirectAttributes redirectAttributes) {
        appointmentService.cancelAppointment(id, userDetails.getId(), reason);
        redirectAttributes.addFlashAttribute("successMessage", "Hủy lịch hẹn thành công!");
        return "redirect:/customer/appointments";
    }
}