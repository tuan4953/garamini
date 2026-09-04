package com.garage.promotion.controller;

import com.garage.promotion.dto.PromotionRequest;
import com.garage.promotion.model.Promotion;
import com.garage.promotion.service.PromotionService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

@Controller
@RequestMapping("/admin/promotions")
@RequiredArgsConstructor
public class PromotionManagementController {

    private final PromotionService promotionService;

    // Danh sách khuyến mãi trong Admin
    @GetMapping
    public String listPromotions(Model model) {
        model.addAttribute("promotions", promotionService.getAllPromotionsForAdmin());
        return "admin/promotion-management";
    }

    // Form thêm mới
    @GetMapping("/create")
    public String createForm(Model model) {
        model.addAttribute("promotionRequest", new PromotionRequest());
        return "admin/promotion-form";
    }

    // Form cập nhật
    @GetMapping("/edit/{id}")
    public String editForm(@PathVariable Long id, Model model) {
        Promotion p = promotionService.getPromotionById(id);
        PromotionRequest request = new PromotionRequest(
                p.getId(),
                p.getCode(),
                p.getTitle(),
                p.getDescription(),
                p.getDiscountPercent(),
                p.getDiscountAmount(),
                p.getStartDate(),
                p.getEndDate(),
                p.getActive()
        );
        model.addAttribute("promotionRequest", request);
        return "admin/promotion-form";
    }

    // Lưu / Cập nhật
    @PostMapping("/save")
    public String savePromotion(@Valid @ModelAttribute("promotionRequest") PromotionRequest request,
                                BindingResult bindingResult,
                                RedirectAttributes redirectAttributes) {
        if (bindingResult.hasErrors()) {
            return "admin/promotion-form";
        }
        try {
            promotionService.saveOrUpdatePromotion(request);
            redirectAttributes.addFlashAttribute("successMessage", "Lưu mã khuyến mãi thành công!");
        } catch (IllegalArgumentException e) {
            redirectAttributes.addFlashAttribute("errorMessage", e.getMessage());
            return "admin/promotion-form";
        }
        return "redirect:/admin/promotions";
    }

    // Bật/Tắt trạng thái
    @PostMapping("/toggle-status/{id}")
    public String toggleStatus(@PathVariable Long id, RedirectAttributes redirectAttributes) {
        promotionService.toggleStatus(id);
        redirectAttributes.addFlashAttribute("successMessage", "Đã đổi trạng thái thành công!");
        return "redirect:/admin/promotions";
    }

    // Xóa chương trình khuyến mãi
    @PostMapping("/delete/{id}")
    public String deletePromotion(@PathVariable Long id, RedirectAttributes redirectAttributes) {
        promotionService.deletePromotion(id);
        redirectAttributes.addFlashAttribute("successMessage", "Xóa chương trình khuyến mãi thành công!");
        return "redirect:/admin/promotions";
    }
}