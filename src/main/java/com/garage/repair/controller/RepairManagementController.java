package com.garage.repair.controller;

import com.garage.repair.dto.*;
import com.garage.repair.model.RepairOrder.RepairStatus;
import com.garage.repair.service.RepairService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Sort;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

@Controller
@RequestMapping("/admin/repairs")
@RequiredArgsConstructor
public class RepairManagementController {

    private final RepairService repairService;

    @GetMapping
    public String listOrders(@RequestParam(required = false) RepairStatus status,
                             @RequestParam(required = false) String keyword,
                             @RequestParam(defaultValue = "0") int page,
                             @RequestParam(defaultValue = "10") int size,
                             Model model) {
        try {
            Page<RepairResponse> ordersPage = repairService.getOrdersPaged(
                    status, keyword, PageRequest.of(page, size, Sort.by("createdAt").descending()));

            model.addAttribute("ordersPage", ordersPage);
            model.addAttribute("statuses", RepairStatus.values());
            model.addAttribute("selectedStatus", status);
            model.addAttribute("keyword", keyword != null ? keyword : "");
            model.addAttribute("currentPage", page);
            model.addAttribute("totalPages", ordersPage.getTotalPages());

            // Dữ liệu thống kê nhanh
            model.addAttribute("stats", repairService.getRepairStats());

            // Dữ liệu cho các Form & Modal
            model.addAttribute("vehicles", repairService.getAllVehicles());
            model.addAttribute("customers", repairService.getAllCustomers());
            model.addAttribute("technicians", repairService.getAllTechnicians());
            model.addAttribute("services", repairService.getAllActiveServices());
            model.addAttribute("spareParts", repairService.getAllActiveSpareParts());

            model.addAttribute("repairRequest", new RepairRequest());
            model.addAttribute("repairUpdateRequest", new RepairUpdateRequest());
        } catch (Exception e) {
            model.addAttribute("errorMessage", "Đã xảy ra lỗi khi tải danh sách phiếu sửa chữa: " + e.getMessage());
            model.addAttribute("ordersPage", Page.empty());
            model.addAttribute("statuses", RepairStatus.values());
            model.addAttribute("selectedStatus", status);
            model.addAttribute("keyword", keyword != null ? keyword : "");
            model.addAttribute("currentPage", 0);
            model.addAttribute("totalPages", 0);
            model.addAttribute("stats", java.util.Collections.emptyMap());
            model.addAttribute("vehicles", java.util.Collections.emptyList());
            model.addAttribute("customers", java.util.Collections.emptyList());
            model.addAttribute("technicians", java.util.Collections.emptyList());
            model.addAttribute("services", java.util.Collections.emptyList());
            model.addAttribute("spareParts", java.util.Collections.emptyList());
            model.addAttribute("repairRequest", new RepairRequest());
            model.addAttribute("repairUpdateRequest", new RepairUpdateRequest());
        }

        return "admin/repairs-list";
    }

    /**
     * API JSON phục vụ lấy chi tiết phiếu để hiển thị trên Modal Xem & Modal Chỉnh sửa
     */
    @GetMapping("/api/{id}")
    @ResponseBody
    public ResponseEntity<RepairResponse> getOrderDetailApi(@PathVariable Long id) {
        try {
            RepairResponse response = repairService.getOrderById(id);
            return ResponseEntity.ok(response);
        } catch (Exception e) {
            return ResponseEntity.notFound().build();
        }
    }

    @GetMapping("/create")
    public String showCreateForm(Model model) {
        model.addAttribute("repairRequest", new RepairRequest());
        model.addAttribute("vehicles", repairService.getAllVehicles());
        model.addAttribute("customers", repairService.getAllCustomers());
        model.addAttribute("technicians", repairService.getAllTechnicians());
        model.addAttribute("services", repairService.getAllActiveServices());
        model.addAttribute("spareParts", repairService.getAllActiveSpareParts());
        model.addAttribute("statuses", RepairStatus.values());
        return "admin/repair-form";
    }

    @PostMapping("/create")
    public String createOrder(@Valid @ModelAttribute("repairRequest") RepairRequest request,
                              BindingResult bindingResult,
                              RedirectAttributes ra) {
        try {
            if (bindingResult.hasErrors()) {
                ra.addFlashAttribute("errorMessage", "Dữ liệu nhập vào chưa hợp lệ, vui lòng kiểm tra lại!");
                return "redirect:/admin/repairs";
            }

            RepairResponse response = repairService.createRepairOrder(request);
            ra.addFlashAttribute("successMessage", "Tạo phiếu sửa chữa thành công! Mã đơn: " + response.getOrderCode());
        } catch (Exception e) {
            ra.addFlashAttribute("errorMessage", "Lỗi khi tạo phiếu sửa chữa: " + e.getMessage());
        }
        return "redirect:/admin/repairs";
    }

    @GetMapping("/edit/{id}")
    public String showEditPage(@PathVariable Long id, Model model, RedirectAttributes ra) {
        try {
            RepairResponse response = repairService.getOrderById(id);
            model.addAttribute("repair", response);
            model.addAttribute("vehicles", repairService.getAllVehicles());
            model.addAttribute("customers", repairService.getAllCustomers());
            model.addAttribute("technicians", repairService.getAllTechnicians());
            model.addAttribute("services", repairService.getAllActiveServices());
            model.addAttribute("spareParts", repairService.getAllActiveSpareParts());
            model.addAttribute("statuses", RepairStatus.values());
            return "admin/repair-edit";
        } catch (Exception e) {
            ra.addFlashAttribute("errorMessage", "Không tìm thấy phiếu sửa chữa: " + e.getMessage());
            return "redirect:/admin/repairs";
        }
    }

    @PostMapping("/{id}/edit")
    public String updateOrder(@PathVariable Long id,
                              @ModelAttribute("repairUpdateRequest") RepairUpdateRequest request,
                              RedirectAttributes ra) {
        try {
            RepairResponse response = repairService.updateRepairOrder(id, request);
            ra.addFlashAttribute("successMessage", "Cập nhật phiếu sửa chữa [" + response.getOrderCode() + "] thành công!");
        } catch (Exception e) {
            ra.addFlashAttribute("errorMessage", "Lỗi khi cập nhật phiếu: " + e.getMessage());
        }
        return "redirect:/admin/repairs";
    }

    @PostMapping("/{id}/update-status")
    public String updateStatus(@PathVariable Long id,
                               @RequestParam RepairStatus status,
                               RedirectAttributes ra) {
        try {
            RepairResponse response = repairService.updateRepairOrder(id, RepairUpdateRequest.builder().status(status).build());
            ra.addFlashAttribute("successMessage", "Đã chuyển trạng thái đơn [" + response.getOrderCode() + "] sang " + status + "!");
        } catch (Exception e) {
            ra.addFlashAttribute("errorMessage", "Lỗi cập nhật trạng thái: " + e.getMessage());
        }
        return "redirect:/admin/repairs";
    }

    @PostMapping("/{id}/delete")
    public String deleteOrder(@PathVariable Long id, RedirectAttributes ra) {
        try {
            repairService.deleteRepairOrder(id);
            ra.addFlashAttribute("successMessage", "Đã xóa phiếu sửa chữa thành công!");
        } catch (Exception e) {
            ra.addFlashAttribute("errorMessage", "Lỗi khi xóa phiếu sửa chữa: " + e.getMessage());
        }
        return "redirect:/admin/repairs";
    }

    @GetMapping("/{id}")
    public String viewDetail(@PathVariable Long id, Model model, RedirectAttributes ra) {
        try {
            RepairResponse response = repairService.getOrderById(id);
            model.addAttribute("repair", response);
            return "admin/repair-detail";
        } catch (Exception e) {
            ra.addFlashAttribute("errorMessage", "Không tìm thấy phiếu sửa chữa: " + e.getMessage());
            return "redirect:/admin/repairs";
        }
    }
}