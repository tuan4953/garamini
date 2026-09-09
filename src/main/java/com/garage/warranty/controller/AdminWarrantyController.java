package com.garage.warranty.controller;

import com.garage.warranty.entity.Warranty;
import com.garage.warranty.repository.WarrantyRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

import java.time.LocalDate;

@Controller
@RequestMapping("/admin/warranty")
@RequiredArgsConstructor
public class AdminWarrantyController {

    // Khai báo field để Lombok tự động inject dependency
    private final WarrantyRepository warrantyRepository;

    @GetMapping
    public String showAdminWarrantyPage(Model model) {
        model.addAttribute("warranties", warrantyRepository.findAll());
        model.addAttribute("newWarranty", new Warranty());
        return "admin/warranty";
    }

    @PostMapping("/create")
    public String createWarranty(@ModelAttribute("newWarranty") Warranty warranty, RedirectAttributes redirectAttributes) {
        if (warranty.getStartDate() == null) {
            warranty.setStartDate(LocalDate.now());
        }
        warrantyRepository.save(warranty);
        redirectAttributes.addFlashAttribute("successMessage", "Tạo thông tin bảo hành thành công!");
        return "redirect:/admin/warranty";
    }

    @PostMapping("/{id}/delete")
    public String deleteWarranty(@PathVariable Long id, RedirectAttributes redirectAttributes) {
        warrantyRepository.deleteById(id);
        redirectAttributes.addFlashAttribute("successMessage", "Xóa thông tin bảo hành thành công!");
        return "redirect:/admin/warranty";
    }
}