package com.garage.review.controller;


import com.garage.review.dto.ReviewRequest;
import com.garage.review.service.ReviewService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

@Controller
@RequestMapping("/reviews")
@RequiredArgsConstructor
public class ReviewController {

    private final ReviewService reviewService;

    @GetMapping
    public String showReviewsPage(Model model) {
        if (!model.containsAttribute("reviewRequest")) {
            model.addAttribute("reviewRequest", new ReviewRequest());
        }
        model.addAttribute("reviews", reviewService.getAllVisibleReviews());
        model.addAttribute("avgRating", reviewService.getAverageRating());
        return "client/reviews";
    }

    @PostMapping("/add")
    public String addReview(@Valid @ModelAttribute("reviewRequest") ReviewRequest request,
                            BindingResult bindingResult,
                            RedirectAttributes redirectAttributes) {
        if (bindingResult.hasErrors()) {
            redirectAttributes.addFlashAttribute("org.springframework.validation.BindingResult.reviewRequest", bindingResult);
            redirectAttributes.addFlashAttribute("reviewRequest", request);
            return "redirect:/reviews";
        }

        reviewService.saveReview(request);
        redirectAttributes.addFlashAttribute("successMessage", "Cảm ơn bạn đã gửi đánh giá dịch vụ!");
        return "redirect:/reviews";
    }
}