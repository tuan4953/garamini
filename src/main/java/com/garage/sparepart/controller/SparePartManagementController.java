package com.garage.sparepart.controller;

import com.garage.sparepart.dto.SparePartRequest;
import com.garage.sparepart.service.SparePartService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Sort;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

@Controller
@RequestMapping("/admin/spare-parts")
@RequiredArgsConstructor
public class SparePartManagementController {

    private final SparePartService sparePartService;

    @GetMapping
    public String listSpareParts(@RequestParam(required = false) Boolean active,
                                 @RequestParam(required = false) String keyword,
                                 @RequestParam(defaultValue = "0") int page,
                                 @RequestParam(defaultValue = "10") int size,
                                 Model model) {
        model.addAttribute("sparePartsPage", sparePartService.getAllSparePartsPaged(
                active, keyword, PageRequest.of(page, size, Sort.by("name").ascending())));
        model.addAttribute("keyword", keyword);
        model.addAttribute("active", active);
        return "admin/spare-parts-list";
    }

    @GetMapping("/create")
    public String showCreateForm(Model model) {
        model.addAttribute("sparePartRequest", new SparePartRequest());
        return "admin/spare-part-form";
    }

    @PostMapping("/create")
    public String createSparePart(@Valid @ModelAttribute("sparePartRequest") SparePartRequest request,
                                  BindingResult result,
                                  RedirectAttributes ra) {
        if (result.hasErrors()) {
            return "admin/spare-part-form";
        }
        sparePartService.createSparePart(request);
        ra.addFlashAttribute("successMessage", "Thêm phụ tùng mới thành công!");
        return "redirect:/admin/spare-parts";
    }

    @GetMapping("/{id}/edit")
    public String showEditForm(@PathVariable Long id, Model model) {
        var sparePart = sparePartService.getSparePartById(id);
        SparePartRequest request = SparePartRequest.builder()
                .name(sparePart.getName())
                .description(sparePart.getDescription())
                .price(sparePart.getPrice())
                .stockQuantity(sparePart.getStockQuantity())
                .unit(sparePart.getUnit())
                .active(sparePart.getActive())
                .build();
        model.addAttribute("sparePartRequest", request);
        model.addAttribute("sparePartId", id);
        return "admin/spare-part-form";
    }

    @PostMapping("/{id}/edit")
    public String updateSparePart(@PathVariable Long id,
                                  @Valid @ModelAttribute("sparePartRequest") SparePartRequest request,
                                  BindingResult result,
                                  RedirectAttributes ra,
                                  Model model) {
        if (result.hasErrors()) {
            model.addAttribute("sparePartId", id);
            return "admin/spare-part-form";
        }
        sparePartService.updateSparePart(id, request);
        ra.addFlashAttribute("successMessage", "Cập nhật phụ tùng thành công!");
        return "redirect:/admin/spare-parts";
    }

    @PostMapping("/{id}/toggle-status")
    public String toggleStatus(@PathVariable Long id, RedirectAttributes ra) {
        sparePartService.toggleSparePartStatus(id);
        ra.addFlashAttribute("successMessage", "Đã đổi trạng thái phụ tùng!");
        return "redirect:/admin/spare-parts";
    }
}