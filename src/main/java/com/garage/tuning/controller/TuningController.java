package com.garage.tuning.controller;

import com.garage.tuning.dto.TuningAppointmentRequest;
import com.garage.tuning.dto.TuningProductResponse;
import com.garage.tuning.service.TuningAppointmentService;
import com.garage.tuning.service.TuningService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

@Controller
@RequestMapping("/tuning")
@RequiredArgsConstructor
public class TuningController {

    private final TuningService tuningService;
    private final TuningAppointmentService tuningAppointmentService;

    // 1. Trang Danh Sách (tuning/list.html)
    @GetMapping({"", "/", "/products"})
    public String getProducts(
            @RequestParam(required = false) Long categoryId,
            @RequestParam(required = false) String keyword,
            @RequestParam(defaultValue = "0") int page,
            @RequestParam(defaultValue = "12") int size,
            Model model) {

        Page<TuningProductResponse> productPage = tuningService.searchProducts(
                categoryId, null, keyword, PageRequest.of(page, size)
        );

        model.addAttribute("tuningServices", productPage.getContent());
        model.addAttribute("currentPage", page);
        model.addAttribute("totalPages", productPage.getTotalPages());
        return "tuning/list";
    }

    // 2. Trang Chi Tiết (tuning/detail.html)
    @GetMapping("/products/{id}")
    public String getProduct(@PathVariable Long id, Model model) {
        TuningProductResponse product = tuningService.getProductById(id);
        model.addAttribute("service", product);
        return "tuning/detail";
    }

    // 3. HIỂN THỊ TRANG BOOKING (Mới thêm) -> Mở tại http://localhost:8080/tuning/booking
    @GetMapping("/booking")
    public String showBookingPage(@RequestParam(required = false) Long serviceId, Model model) {
        model.addAttribute("selectedServiceId", serviceId);

        if (!model.containsAttribute("tuningAppointmentRequest")) {
            model.addAttribute("tuningAppointmentRequest", new TuningAppointmentRequest());
        }

        return "tuning/booking"; // Trỏ đúng file templates/tuning/booking.html
    }

    // 4. XỬ LÝ SUBMIT FORM BOOKING
    @PostMapping("/booking")
    public String createAppointment(
            @Valid @ModelAttribute("tuningAppointmentRequest") TuningAppointmentRequest request,
            BindingResult bindingResult,
            RedirectAttributes redirectAttributes) {

        if (bindingResult.hasErrors()) {
            return "tuning/booking"; // Nếu có lỗi Validation thì giữ lại trang booking
        }

        tuningAppointmentService.createAppointment(request);
        redirectAttributes.addFlashAttribute("successMessage", "Đặt lịch nâng cấp thành công!");
        return "redirect:/tuning";
    }
}