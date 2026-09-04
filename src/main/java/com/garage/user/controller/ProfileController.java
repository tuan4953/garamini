package com.garage.user.controller;

import com.garage.user.dto.UserRequest;
import com.garage.user.dto.UserResponse;
import com.garage.user.service.UserService;
import org.springframework.security.core.Authentication;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;

@Controller
@RequestMapping("/customer")
public class ProfileController {

    private final UserService userService;

    public ProfileController(UserService userService) {
        this.userService = userService;
    }



    @GetMapping("/profile")
    public String profile(
            Authentication authentication,
            Model model
    ) {

        String email = authentication.getName();

        UserResponse user =
                UserResponse.fromEntity(
                        userService.findByEmail(email)
                );

        model.addAttribute("user", user);

        return "customer/profile";
    }

    @PostMapping("/profile")
    public String updateProfile(
            Authentication authentication,
            @ModelAttribute UserRequest request
    ) {

        userService.updateProfile(
                authentication.getName(),
                request
        );

        return "redirect:/customer/profile?success=true";
    }
}