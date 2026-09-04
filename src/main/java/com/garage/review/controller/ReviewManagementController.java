package com.garage.review.controller;




import com.garage.review.service.ReviewService;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

@Controller
@RequestMapping("/admin/reviews")
@RequiredArgsConstructor
public class ReviewManagementController {

    private final ReviewService reviewService;

    @GetMapping
    public String listReviews(Model model) {
        model.addAttribute("reviewList", reviewService.getAllReviewsForAdmin());
        return "admin/review-management";
    }

    @PostMapping("/reply/{id}")
    public String replyReview(@PathVariable Long id,
                              @RequestParam("adminReply") String adminReply,
                              RedirectAttributes redirectAttributes) {
        reviewService.replyToReview(id, adminReply);
        redirectAttributes.addFlashAttribute("successMessage", "Cập nhật phản hồi thành công!");
        return "redirect:/admin/reviews";
    }

    @PostMapping("/toggle-status/{id}")
    public String toggleStatus(@PathVariable Long id, RedirectAttributes redirectAttributes) {
        reviewService.toggleVisibility(id);
        redirectAttributes.addFlashAttribute("successMessage", "Thay đổi trạng thái hiển thị thành công!");
        return "redirect:/admin/reviews";
    }
}
