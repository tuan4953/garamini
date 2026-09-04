package com.garage.tuning.controller;

import com.garage.tuning.dto.TuningAppointmentRequest;
import com.garage.tuning.service.TuningAppointmentService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

@Controller
@RequestMapping("/tuning/appointments")
@RequiredArgsConstructor
public class TuningAppointmentController {

    private final TuningAppointmentService tuningAppointmentService;

    // Bổ sung thêm /booking trực tiếp nếu khách bấm từ menu chính
    @GetMapping({"", "/", "/booking"})
    public String showBookingForm(@RequestParam(required = false) Long serviceId, Model model) {
        model.addAttribute("selectedServiceId", serviceId);
        if (!model.containsAttribute("tuningAppointmentRequest")) {
            model.addAttribute("tuningAppointmentRequest", new TuningAppointmentRequest());
        }
        return "tuning/booking"; // Trỏ đúng file templates/tuning/booking.html
    }

    @PostMapping("/create")
    public String bookAppointment(@Valid @ModelAttribute("tuningAppointmentRequest") TuningAppointmentRequest request,
                                  BindingResult bindingResult,
                                  RedirectAttributes ra) {
        if (bindingResult.hasErrors()) {
            return "tuning/booking";
        }
        tuningAppointmentService.createTuningAppointment(request);
        ra.addFlashAttribute("successMessage", "Đặt lịch độ xe thành công!");
        return "redirect:/tuning";
    }
}