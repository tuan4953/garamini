package com.garage.appointment.controller;


import com.garage.appointment.dto.AppointmentResponse;
import com.garage.appointment.dto.AppointmentUpdateRequest;
import com.garage.appointment.model.Appointment;
import com.garage.appointment.service.AppointmentService;
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
@RequestMapping("/admin/appointments")
@RequiredArgsConstructor
public class AppointmentManagementController {

    private final AppointmentService appointmentService;

    @GetMapping
    public String listAllAppointments(@RequestParam(required = false) Appointment.AppointmentStatus status,
                                      @RequestParam(required = false) String keyword,
                                      @RequestParam(defaultValue = "0") int page,
                                      @RequestParam(defaultValue = "10") int size,
                                      Model model) {
        Page<AppointmentResponse> appointmentPage = appointmentService.getAllAppointmentsPaged(
                status, keyword, PageRequest.of(page, size, Sort.by("appointmentDate").descending()));

        model.addAttribute("appointmentPage", appointmentPage);
        model.addAttribute("statuses", Appointment.AppointmentStatus.values());
        model.addAttribute("selectedStatus", status);
        model.addAttribute("keyword", keyword);
        return "admin/appointments";
    }

    @GetMapping("/edit/{id}")
    public String showEditForm(@PathVariable Long id, Model model) {
        AppointmentResponse response = appointmentService.getAppointmentById(id);

        AppointmentUpdateRequest request = AppointmentUpdateRequest.builder()
                .id(response.getId())
                .appointmentDate(response.getAppointmentDate())
                .status(response.getStatus())
                .notes(response.getNotes())
                .cancellationReason(response.getCancellationReason())
                .build();

        model.addAttribute("appointmentUpdateRequest", request);
        model.addAttribute("appointmentDetail", response);
        model.addAttribute("statuses", Appointment.AppointmentStatus.values());
        return "admin/appointment-form";
    }

    @PostMapping("/edit/{id}")
    public String updateAppointment(@PathVariable Long id,
                                    @Valid @ModelAttribute("appointmentUpdateRequest") AppointmentUpdateRequest request,
                                    BindingResult bindingResult,
                                    Model model,
                                    RedirectAttributes redirectAttributes) {
        if (bindingResult.hasErrors()) {
            model.addAttribute("appointmentDetail", appointmentService.getAppointmentById(id));
            model.addAttribute("statuses", Appointment.AppointmentStatus.values());
            return "admin/appointment-form";
        }

        appointmentService.updateAppointment(request);
        redirectAttributes.addFlashAttribute("successMessage", "Cập nhật thông tin lịch hẹn thành công!");
        return "redirect:/admin/appointments";
    }
}