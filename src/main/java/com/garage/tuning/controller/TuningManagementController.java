package com.garage.tuning.controller;

import com.garage.tuning.dto.TuningProductRequest;
import com.garage.tuning.repository.TuningCategoryRepository;
import com.garage.tuning.service.TuningService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.PageRequest;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

@Controller
@RequestMapping("/admin/tuning/products")
@RequiredArgsConstructor
public class TuningManagementController {

    private final TuningService tuningService;
    private final TuningCategoryRepository categoryRepository;

    @GetMapping
    public String listProducts(@RequestParam(required = false) Long categoryId,
                               @RequestParam(required = false) Boolean active,
                               @RequestParam(required = false) String keyword,
                               @RequestParam(defaultValue = "0") int page,
                               @RequestParam(defaultValue = "10") int size,
                               Model model) {
        model.addAttribute("productsPage", tuningService.searchProducts(categoryId, active, keyword, PageRequest.of(page, size)));
        model.addAttribute("categories", categoryRepository.findByActiveTrueOrderByNameAsc());
        return "admin/tuning-products-list";
    }

    @GetMapping("/create")
    public String showCreateForm(Model model) {
        model.addAttribute("productRequest", new TuningProductRequest());
        model.addAttribute("categories", categoryRepository.findByActiveTrueOrderByNameAsc());
        return "admin/tuning-product-form";
    }

    @PostMapping("/create")
    public String createProduct(@Valid @ModelAttribute("productRequest") TuningProductRequest request,
                                BindingResult result,
                                RedirectAttributes ra,
                                Model model) {
        if (result.hasErrors()) {
            model.addAttribute("categories", categoryRepository.findByActiveTrueOrderByNameAsc());
            return "admin/tuning-product-form";
        }
        tuningService.createProduct(request);
        ra.addFlashAttribute("successMessage", "Thêm sản phẩm độ xe thành công!");
        return "redirect:/admin/tuning/products";
    }
}