package com.garage.vehicle.controller;

import org.springframework.ui.Model;
import com.garage.security.CustomUserDetails;
import com.garage.vehicle.dto.VehicleRequest;
import com.garage.vehicle.dto.VehicleResponse;
import com.garage.vehicle.service.VehicleService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.stereotype.Controller;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.List;
import java.util.UUID;

@Controller
@RequestMapping("/customer/vehicles")
@RequiredArgsConstructor
public class VehicleController {

    private final VehicleService vehicleService;

    // Đường dẫn lưu file ảnh trên máy chủ
    private static final String UPLOAD_DIR = "uploads/vehicles/";

    @GetMapping
    public String listCustomerVehicles(@AuthenticationPrincipal CustomUserDetails userDetails, Model model) {
        List<VehicleResponse> vehicles = vehicleService.getVehiclesByOwner(userDetails.getId());
        model.addAttribute("vehicles", vehicles);

        VehicleRequest request = new VehicleRequest();
        request.setOwnerId(userDetails.getId());
        model.addAttribute("vehicleRequest", request);

        return "customer/vehicles";
    }

    @GetMapping("/{id}")
    public String vehicleDetail(@PathVariable Long id, Model model) {
        VehicleResponse vehicle = vehicleService.getVehicleById(id);
        model.addAttribute("vehicle", vehicle);
        return "vehicle/detail";
    }

    @GetMapping("/add")
    public String showAddForm(Model model, @AuthenticationPrincipal CustomUserDetails userDetails) {
        VehicleRequest request = new VehicleRequest();
        request.setOwnerId(userDetails.getId());
        model.addAttribute("vehicleRequest", request);
        return "customer/vehicles";
    }

    @PostMapping("/add")
    public String createVehicle(@Valid @ModelAttribute("vehicleRequest") VehicleRequest request,
                                BindingResult bindingResult,
                                @RequestParam(value = "imageFile", required = false) MultipartFile imageFile,
                                @AuthenticationPrincipal CustomUserDetails userDetails,
                                Model model,
                                RedirectAttributes redirectAttributes) {

        request.setOwnerId(userDetails.getId());

        // Kiểm tra lỗi validation DTO
        if (bindingResult.hasErrors()) {
            List<VehicleResponse> vehicles = vehicleService.getVehiclesByOwner(userDetails.getId());
            model.addAttribute("vehicles", vehicles);
            model.addAttribute("openAddModal", true); // Đánh dấu để JavaScript mở lại Modal
            return "customer/vehicles";
        }

        // 1. Xử lý lưu file ảnh upload
        if (imageFile != null && !imageFile.isEmpty()) {
            try {
                Path uploadPath = Paths.get(UPLOAD_DIR);
                if (!Files.exists(uploadPath)) {
                    Files.createDirectories(uploadPath);
                }

                String originalFilename = imageFile.getOriginalFilename();
                String fileExtension = "";
                if (originalFilename != null && originalFilename.contains(".")) {
                    fileExtension = originalFilename.substring(originalFilename.lastIndexOf("."));
                }
                String newFileName = UUID.randomUUID().toString() + fileExtension;

                Path filePath = uploadPath.resolve(newFileName);
                Files.copy(imageFile.getInputStream(), filePath);

                request.setImageUrl("/" + UPLOAD_DIR + newFileName);

            } catch (IOException e) {
                e.printStackTrace();
                bindingResult.rejectValue("imageUrl", "error.vehicleRequest", "Lỗi lưu file ảnh!");

                List<VehicleResponse> vehicles = vehicleService.getVehiclesByOwner(userDetails.getId());
                model.addAttribute("vehicles", vehicles);
                model.addAttribute("openAddModal", true);
                return "customer/vehicles";
            }
        } else {
            // 2. Tự động gán ảnh mặc định nếu không upload
            request.setImageUrl("/images/default-vehicle.png");
        }

        vehicleService.createVehicle(request);

        redirectAttributes.addFlashAttribute("successMessage", "Thêm phương tiện thành công!");
        return "redirect:/customer/vehicles";
    }
}