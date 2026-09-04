package com.garage.user.controller;

import com.garage.user.model.User;
import com.garage.user.service.UserService;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

import java.util.List;

@Controller
@RequestMapping("/admin/users")
public class UserManagementController {

    private final UserService userService;

    public UserManagementController(UserService userService) {
        this.userService = userService;
    }

    // 1. Hiển thị danh sách tất cả người dùng
    @GetMapping
    public String listUsers(Model model) {
        List<User> users = userService.getAllUsers();
        model.addAttribute("users", users);
        return "admin/users"; // Trả về trang giao diện admin/users.html
    }

    // 2. Hiển thị form thêm người dùng mới
    @GetMapping("/create")
    public String showCreateForm(Model model) {
        model.addAttribute("user", new User());
        model.addAttribute("pageTitle", "Thêm Người Dùng Mới");
        return "admin/user-form"; // Trả về trang giao diện admin/user-form.html
    }

    // 3. Hiển thị form cập nhật thông tin người dùng
    @GetMapping("/edit/{id}")
    public String showEditForm(@PathVariable("id") Long id, Model model, RedirectAttributes redirectAttributes) {
        try {
            User user = userService.getUserById(id);
            model.addAttribute("user", user);
            model.addAttribute("pageTitle", "Chỉnh Sửa Người Dùng (ID: " + id + ")");
            return "admin/user-form";
        } catch (RuntimeException e) {
            redirectAttributes.addFlashAttribute("errorMessage", e.getMessage());
            return "redirect:/admin/users";
        }
    }

    // 4. Lưu thông tin (Thêm mới hoặc Cập nhật)
    @PostMapping("/save")
    public String saveUser(@ModelAttribute("user") User user, RedirectAttributes redirectAttributes) {
        try {
            userService.saveOrUpdateUser(user);
            redirectAttributes.addFlashAttribute("successMessage", "Lưu thông tin người dùng thành công!");
        } catch (Exception e) {
            redirectAttributes.addFlashAttribute("errorMessage", "Lỗi khi lưu người dùng: " + e.getMessage());
        }
        return "redirect:/admin/users";
    }

    // 5. Đổi trạng thái Tài khoản (Khóa / Mở khóa)
    @PostMapping("/{id}/toggle-status")
    public String toggleUserStatus(@PathVariable("id") Long id, RedirectAttributes redirectAttributes) {
        try {
            userService.toggleUserStatus(id);
            redirectAttributes.addFlashAttribute("successMessage", "Cập nhật trạng thái người dùng thành công!");
        } catch (Exception e) {
            redirectAttributes.addFlashAttribute("errorMessage", "Không thể thay đổi trạng thái: " + e.getMessage());
        }
        return "redirect:/admin/users";
    }

    // 6. Xóa người dùng
    @PostMapping("/{id}/delete")
    public String deleteUser(@PathVariable("id") Long id, RedirectAttributes redirectAttributes) {
        try {
            userService.deleteUser(id);
            redirectAttributes.addFlashAttribute("successMessage", "Xóa người dùng thành công!");
        } catch (Exception e) {
            redirectAttributes.addFlashAttribute("errorMessage", "Không thể xóa người dùng: " + e.getMessage());
        }
        return "redirect:/admin/users";
    }
}