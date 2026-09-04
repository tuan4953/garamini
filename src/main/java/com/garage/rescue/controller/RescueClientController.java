package com.garage.rescue.controller;

import com.garage.rescue.dto.RescueBookingDto;
import com.garage.rescue.service.RescueService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

@Controller
@RequestMapping("/rescue")
@RequiredArgsConstructor
public class RescueClientController {

    private final RescueService rescueService;

    // 1. Điều hướng tới trang Danh sách dịch vụ cứu hộ (templates/rescue/list.html)
    @GetMapping({"", "/", "/list"})
    public String showRescueList(Model model) {
        // Lấy danh sách gói/dịch vụ cứu hộ hiển thị cho khách
        model.addAttribute("rescueServices", rescueService.getAllActiveServices());
        return "rescue/list";
    }

    // 2. Điều hướng tới trang Chi tiết gói cứu hộ (templates/rescue/detail.html)
    @GetMapping("/detail/{id}")
    public String showRescueDetail(@PathVariable Long id, Model model) {
        model.addAttribute("service", rescueService.getServiceById(id));
        return "rescue/detail";
    }

    // 3. Điều hướng tới Form Đặt lịch cứu hộ (templates/rescue/booking.html)
    @GetMapping("/booking")
    public String showBookingForm(@RequestParam(required = false) Long serviceId, Model model) {
        model.addAttribute("rescueServices", rescueService.getAllActiveServices());
        model.addAttribute("selectedServiceId", serviceId);
        return "rescue/booking";
    }

    // 4. Xử lý Submit Form Đặt lịch cứu hộ khẩn cấp
    @PostMapping("/booking")
    public String submitRescueBooking(@Valid @ModelAttribute("rescueBooking") RescueBookingDto bookingDto,
                                      BindingResult bindingResult,
                                      RedirectAttributes ra) {
        if (bindingResult.hasErrors()) {
            return "rescue/booking";
        }

        // Gọi hàm xử lý từ Service
        rescueService.createBookingFromClient(bookingDto);

        ra.addFlashAttribute("successMessage", "Đã gửi yêu cầu cứu hộ thành công!");
        return "redirect:/rescue/booking";
    }
}