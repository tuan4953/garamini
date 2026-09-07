package com.garage.appointment.controller;

import com.garage.appointment.dto.AppointmentRequest;
import com.garage.appointment.dto.AppointmentResponse;
import com.garage.appointment.service.AppointmentService;
import com.garage.security.CustomUserDetails;
import com.garage.service.repository.ServiceRepository; // ⚠️ đổi đúng package thực tế
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
    private final ServiceRepository serviceRepository;

    @GetMapping("/appointments")
    public String showPublicAppointmentForm(@AuthenticationPrincipal CustomUserDetails userDetails, Model model) {
        AppointmentRequest request = new AppointmentRequest();
        List<VehicleResponse> vehicles = Collections.emptyList();

        if (userDetails != null) {
            vehicles = vehicleService.getVehiclesByOwner(userDetails.getId());
        }

        model.addAttribute("appointmentRequest", request);
        model.addAttribute("vehicles", vehicles);
        model.addAttribute("services", serviceRepository.findAll());
        return "customer/appointments";
    }

    @GetMapping("/customer/appointments")
    public String listCustomerAppointments(@RequestParam(required = false) Long vehicleId,
                                           @AuthenticationPrincipal CustomUserDetails userDetails,
                                           Model model) {
        List<AppointmentResponse> appointments = appointmentService.getAppointmentsByCustomer(userDetails.getId());
        model.addAttribute("appointments", appointments);

        List<VehicleResponse> vehicles = vehicleService.getVehiclesByOwner(userDetails.getId());
        model.addAttribute("vehicles", vehicles);
        model.addAttribute("services", serviceRepository.findAll());

        AppointmentRequest request = new AppointmentRequest();
        if (vehicleId != null) {
            request.setVehicleId(vehicleId);
        }
        model.addAttribute("appointmentRequest", request);

        return "customer/appointments";
    }

    @PostMapping("/customer/appointments/create")
    public String createAppointment(@Valid @ModelAttribute("appointmentRequest") AppointmentRequest request,
                                    BindingResult bindingResult,
                                    @AuthenticationPrincipal CustomUserDetails userDetails,
                                    Model model,
                                    RedirectAttributes redirectAttributes) {
        if (bindingResult.hasErrors()) {
            model.addAttribute("appointments", appointmentService.getAppointmentsByCustomer(userDetails.getId()));
            model.addAttribute("vehicles", vehicleService.getVehiclesByOwner(userDetails.getId()));
            model.addAttribute("services", serviceRepository.findAll());
            return "customer/appointments";
        }

        appointmentService.createAppointment(userDetails.getId(), request);
        redirectAttributes.addFlashAttribute("successMessage", "Đặt lịch hẹn thành công!");
        return "redirect:/customer/appointments";
    }

    @PostMapping("/customer/appointments/{id}/cancel")
    public String cancelAppointment(@PathVariable Long id,
                                    @RequestParam String reason,
                                    @AuthenticationPrincipal CustomUserDetails userDetails,
                                    RedirectAttributes redirectAttributes) {
        appointmentService.cancelAppointment(id, userDetails.getId(), reason);
        redirectAttributes.addFlashAttribute("successMessage", "Hủy lịch hẹn thành công!");
        return "redirect:/customer/appointments";
    }

    @GetMapping("/appointments/new")
    public String showRepairAppointmentForm(@RequestParam(required = false) Long vehicleId,
                                            @AuthenticationPrincipal CustomUserDetails userDetails,
                                            Model model) {
        AppointmentRequest request = new AppointmentRequest();
        if (vehicleId != null) {
            request.setVehicleId(vehicleId);
        }

        List<VehicleResponse> vehicles = Collections.emptyList();
        if (userDetails != null) {
            vehicles = vehicleService.getVehiclesByOwner(userDetails.getId());
        }

        model.addAttribute("appointmentRequest", request);
        model.addAttribute("vehicles", vehicles);
        model.addAttribute("services", serviceRepository.findAll());
        return "customer/appointments-new";
    }

    @GetMapping("/customer/appointments/create")
    public String showCreateFormDirectly(@RequestParam(required = false) Long vehicleId,
                                         @AuthenticationPrincipal CustomUserDetails userDetails,
                                         Model model) {
        AppointmentRequest request = new AppointmentRequest();
        if (vehicleId != null) {
            request.setVehicleId(vehicleId);
        }

        List<VehicleResponse> vehicles = Collections.emptyList();
        if (userDetails != null) {
            vehicles = vehicleService.getVehiclesByOwner(userDetails.getId());
        }

        model.addAttribute("appointmentRequest", request);
        model.addAttribute("vehicles", vehicles);
        model.addAttribute("services", serviceRepository.findAll());

        return "customer/appointment-create";
    }
    @GetMapping("/booking")
    public String showBookingPage(@RequestParam(required = false) Long vehicleId,
                                  @AuthenticationPrincipal CustomUserDetails userDetails,
                                  Model model) {
        AppointmentRequest request = new AppointmentRequest();
        if (vehicleId != null) {
            request.setVehicleId(vehicleId);
        }

        List<VehicleResponse> vehicles = Collections.emptyList();
        if (userDetails != null) {
            vehicles = vehicleService.getVehiclesByOwner(userDetails.getId());
        }

        model.addAttribute("appointmentRequest", request);
        model.addAttribute("vehicles", vehicles);
        model.addAttribute("services", serviceRepository.findAll());

        // Trả về view tạo lịch hẹn trực tiếp
        return "customer/appointment-create";
    }


}
