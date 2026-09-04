package com.garage.promotion.controller;


import com.garage.promotion.service.PromotionService;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;

@Controller
@RequestMapping("/promotions")
@RequiredArgsConstructor
public class PromotionController {

    private final PromotionService promotionService;

    // Trang xem danh sách mã ưu đãi công khai
    @GetMapping
    public String showPromotionsPage(Model model) {
        model.addAttribute("promotions", promotionService.getActivePromotions());
        return "client/promotions";
    }
}