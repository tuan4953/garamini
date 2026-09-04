package com.garage.repair.controller;

import com.garage.repair.dto.AssignedJobResponse;
import com.garage.repair.service.RepairHistoryService;
import lombok.RequiredArgsConstructor;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@Controller
@RequestMapping
@RequiredArgsConstructor
public class RepairHistoryController {

    private final RepairHistoryService repairHistoryService;

    // 1. Lịch sử theo Xe
    @GetMapping("/repair-history/vehicle/{vehicleId}")
    public String getVehicleHistory(@PathVariable Long vehicleId, Model model) {
        model.addAttribute("historyList", repairHistoryService.getVehicleRepairHistory(vehicleId));
        return "repair/history-vehicle";
    }

    // 2. Lịch sử theo Khách hàng
    @GetMapping("/repair-history/my-history")
    public String getMyHistory(@RequestParam Long customerId, Model model) {
        model.addAttribute("historyList", repairHistoryService.getCustomerRepairHistory(customerId));
        return "repair/history-customer";
    }

    // 3. Khách hàng truy cập trực tiếp
    @GetMapping("/customer/repair-history")
    public String getCustomerRepairHistoryDirect(
            @RequestParam(required = false, defaultValue = "1") Long customerId,
            Model model) {

        model.addAttribute("historyList", repairHistoryService.getCustomerRepairHistory(customerId));
        return "repair/history-customer";
    }

    // 4. Xử lý URL /technician/assigned-jobs
    @GetMapping("/technician/assigned-jobs")
    public String getAssignedJobs(@AuthenticationPrincipal UserDetails userDetails, Model model) {
        String email = userDetails.getUsername();
        List<AssignedJobResponse> jobs = repairHistoryService.getAssignedJobsByTechnicianEmail(email);

        model.addAttribute("jobs", jobs);
        return "technician/assigned-jobs";
    }
}