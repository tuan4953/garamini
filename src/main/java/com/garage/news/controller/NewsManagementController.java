package com.garage.news.controller;

import com.garage.news.dto.NewsRequest;
import com.garage.news.model.News;
import com.garage.news.service.NewsService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

@Controller
@RequestMapping("/admin/news")
@RequiredArgsConstructor
public class NewsManagementController {

    private final NewsService newsService;

    // Danh sách bài viết trong Admin
    @GetMapping
    public String listNews(Model model) {
        model.addAttribute("newsList", newsService.getAllNewsForAdmin());
        return "admin/news-management";
    }

    // Form thêm bài viết mới
    @GetMapping("/create")
    public String createForm(Model model) {
        model.addAttribute("newsRequest", new NewsRequest());
        return "admin/news-form";
    }

    // Form chỉnh sửa bài viết
    @GetMapping("/edit/{id}")
    public String editForm(@PathVariable Long id, Model model) {
        News news = newsService.getNewsById(id);
        NewsRequest request = new NewsRequest(
                news.getId(),
                news.getTitle(),
                news.getSummary(),
                news.getContent(),
                news.getImageUrl(),
                news.getPublished()
        );
        model.addAttribute("newsRequest", request);
        return "admin/news-form";
    }

    // Lưu / Cập nhật bài viết
    @PostMapping("/save")
    public String saveNews(@Valid @ModelAttribute("newsRequest") NewsRequest request,
                           BindingResult bindingResult,
                           RedirectAttributes redirectAttributes) {
        if (bindingResult.hasErrors()) {
            return "admin/news-form";
        }
        newsService.saveOrUpdateNews(request);
        redirectAttributes.addFlashAttribute("successMessage", "Đã lưu bài viết thành công!");
        return "redirect:/admin/news";
    }

    // Ẩn / Hiện bài viết
    @PostMapping("/toggle-status/{id}")
    public String toggleStatus(@PathVariable Long id, RedirectAttributes redirectAttributes) {
        newsService.togglePublishStatus(id);
        redirectAttributes.addFlashAttribute("successMessage", "Thay đổi trạng thái xuất bản thành công!");
        return "redirect:/admin/news";
    }

    // Xóa bài viết
    @PostMapping("/delete/{id}")
    public String deleteNews(@PathVariable Long id, RedirectAttributes redirectAttributes) {
        newsService.deleteNews(id);
        redirectAttributes.addFlashAttribute("successMessage", "Xóa bài viết thành công!");
        return "redirect:/admin/news";
    }
}
