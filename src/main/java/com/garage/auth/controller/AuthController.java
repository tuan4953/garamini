package com.garage.auth.controller;

import com.garage.auth.dto.RegisterRequest;
import com.garage.auth.service.AuthService;
import jakarta.validation.Valid;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.PostMapping;

@Controller
public class AuthController {

    private final AuthService authService;

    public AuthController(AuthService authService) {
        this.authService = authService;
    }

    @GetMapping("/login")
    public String login(jakarta.servlet.http.HttpServletRequest request) {
        // Chủ động tạo Session trước để tránh lỗi "response has been committed" khi Spring Security chèn CSRF token
        request.getSession(true);
        return "auth/login";
    }

    @GetMapping("/register")
    public String registerPage(Model model) {

        model.addAttribute(
                "registerRequest",
                new RegisterRequest()
        );

        return "auth/register";
    }

    @PostMapping("/register")
    public String register(
            @Valid
            @ModelAttribute("registerRequest")
            RegisterRequest request,

            BindingResult bindingResult,

            Model model
    ) {

        if (bindingResult.hasErrors()) {
            return "auth/register";
        }

        try {

            authService.register(request);

            return "redirect:/login?registered=true";

        } catch (IllegalArgumentException e) {

            model.addAttribute(
                    "registerError",
                    e.getMessage()
            );

            return "auth/register";
        }
    }

    @GetMapping("/forgot-password")
    public String forgotPassword() {
        return "auth/forgot-password";
    }

    @GetMapping("/reset-password")
    public String resetPassword() {
        return "auth/reset-password";
    }

    @GetMapping("/access-denied")
    public String accessDenied() {
        return "auth/access-denied";
    }
}