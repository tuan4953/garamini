package com.garage.news.controller;


import com.garage.news.service.NewsService;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;

@Controller
@RequestMapping("/news")
@RequiredArgsConstructor
public class NewsController {

    private final NewsService newsService;

    // Danh sách bài viết công khai
    @GetMapping
    public String showNewsListPage(Model model) {
        model.addAttribute("newsList", newsService.getAllPublishedNews());
        return "client/news-list";
    }

    // Chi tiết 1 bài viết
    @GetMapping("/{id}")
    public String showNewsDetailPage(@PathVariable Long id, Model model) {
        model.addAttribute("news", newsService.getNewsById(id));
        return "client/news-detail";
    }
}