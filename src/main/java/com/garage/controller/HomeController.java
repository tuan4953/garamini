package com.garage.controller;

import com.garage.news.service.NewsService;
import com.garage.promotion.service.PromotionService;
import com.garage.review.service.ReviewService;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;

@Controller
@RequiredArgsConstructor
public class HomeController {

    private final PromotionService promotionService;
    private final NewsService newsService;
    private final ReviewService reviewService;

    @GetMapping({"/", "/home"})
    public String index(Model model) {
        // Load một số dữ liệu nổi bật ra trang chủ
        model.addAttribute("promotions", promotionService.getActivePromotions());
        model.addAttribute("newsList", newsService.getAllPublishedNews());
        model.addAttribute("reviews", reviewService.getAllVisibleReviews());
        model.addAttribute("avgRating", reviewService.getAverageRating());
        return "client/index";
    }
}