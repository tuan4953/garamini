package com.garage.sparepart.controller;

import com.garage.sparepart.dto.SparePartResponse;
import com.garage.sparepart.service.SparePartService;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;

import java.util.List;

@Controller
@RequestMapping("/spare-parts")
@RequiredArgsConstructor
public class SparePartWebController {

    private final SparePartService sparePartService;

    // Hiển thị danh sách phụ tùng -> templates/spare-part/list.html
    @GetMapping({"", "/"})
    public String listSpareParts(Model model) {
        List<SparePartResponse> spareParts = sparePartService.getAllActiveSpareParts();
        model.addAttribute("spareParts", spareParts);
        return "spare-part/list";
    }

    // Hiển thị chi tiết phụ tùng -> templates/spare-part/detail.html
    @GetMapping("/{id}")
    public String sparePartDetail(@PathVariable Long id, Model model) {
        SparePartResponse sparePart = sparePartService.getSparePartById(id);
        model.addAttribute("sparePart", sparePart);
        return "spare-part/detail";
    }
}