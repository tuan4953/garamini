package com.garage.service.controller;

import com.garage.service.dto.ServiceResponse;
import com.garage.service.service.ServiceManagementService;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;

import java.util.List;

@Controller
@RequestMapping("/services")
@RequiredArgsConstructor
public class ServiceController {

    private final ServiceManagementService serviceManagementService;

    // 1. Đường dẫn cho Khách hàng -> Trả về src/main/resources/templates/service/list.html
    // Truy cập: http://localhost:8080/services
    @GetMapping({"", "/"})
    public String listServices(Model model) {
        List<ServiceResponse> services = serviceManagementService.getAllActiveServices();
        model.addAttribute("services", services);
        return "service/list";
    }

    // 2. Đường dẫn cho Kỹ thuật viên -> Trả về src/main/resources/templates/service/technician-list.html
    // Truy cập: http://localhost:8080/services/technician
    @GetMapping("/technician")
    public String listServicesForTechnician(Model model) {
        List<ServiceResponse> services = serviceManagementService.getAllActiveServices();
        model.addAttribute("services", services);
        return "service/technician-list";
    }

    // 3. Đường dẫn xem chi tiết dịch vụ -> Trả về src/main/resources/templates/service/detail.html
    // Truy cập: http://localhost:8080/services/{id}
    @GetMapping("/{id}")
    public String serviceDetail(@PathVariable Long id, Model model) {
        ServiceResponse service = serviceManagementService.getServiceById(id);
        model.addAttribute("service", service);
        return "service/detail";
    }
}