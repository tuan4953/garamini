package com.garage.repair.controller;

import com.garage.repair.dto.DiagnosisRequest;
import com.garage.repair.dto.InspectionRequest;
import com.garage.repair.service.RepairService;
import lombok.RequiredArgsConstructor;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

@Controller
@RequestMapping
@RequiredArgsConstructor
public class RepairController {

    private final RepairService repairService;

    // Xem chi tiết đơn sửa chữa
    @GetMapping("/repairs/{id}")
    public String viewRepairDetail(@PathVariable Long id, Model model) {
        model.addAttribute("repair", repairService.getOrderById(id));
        return "repair/detail";
    }

    // 1. Hiển thị form tiếp nhận xe (/technician/intake)
    @GetMapping("/technician/intake")
    public String showIntakeForm(Model model) {
        model.addAttribute("inspectionRequest", new InspectionRequest());
        return "technician/intake";
    }

    // 2. Xử lý submit tiếp nhận xe
    @PostMapping("/technician/intake")
    public String processIntake(
            @ModelAttribute("inspectionRequest") InspectionRequest request,
            RedirectAttributes redirectAttributes) {

        // CHỈ TRUYỀN 1 THAM SỐ (xóa userDetails.getUsername())
        repairService.createInspection(request);

        redirectAttributes.addFlashAttribute("successMessage", "Tiếp nhận xe thành công!");
        return "redirect:/technician/assigned-jobs";
    }
    @GetMapping("/technician/diagnosis")
    public String showDiagnosisForm(
            @RequestParam(required = false) Long orderId,
            Model model) {

        DiagnosisRequest request = new DiagnosisRequest();
        if (orderId != null) {
            request.setRepairOrderId(orderId);
        }

        model.addAttribute("diagnosisRequest", request);
        return "technician/diagnosis"; // Trỏ đến templates/technician/diagnosis.html
    }

    // 2. Xử lý submit thông tin chẩn đoán
    @PostMapping("/technician/diagnosis")
    public String processDiagnosis(
            @ModelAttribute("diagnosisRequest") DiagnosisRequest request,
            RedirectAttributes redirectAttributes) {

        // Gọi hàm lưu chẩn đoán trong RepairService (hoặc xử lý theo logic dịch vụ của bạn)
        repairService.saveDiagnosis(request);

        redirectAttributes.addFlashAttribute("successMessage", "Đã lưu kết quả chẩn đoán thành công!");
        return "redirect:/technician/assigned-jobs";
    }
    // Bổ sung vào class RepairController

    // 1. Hiển thị trang ghi chú KTV
    @GetMapping("/technician/notes")
    public String showNotesForm(@RequestParam(required = false) Long orderId, Model model) {
        InspectionRequest request = new InspectionRequest();
        if (orderId != null) {
            request.setVehicleId(orderId); // Tận dụng field Id sẵn có
        }
        model.addAttribute("noteRequest", request);
        return "technician/notes"; // Trỏ đến templates/technician/notes.html
    }

    // 2. Xử lý lưu ghi chú
    @PostMapping("/technician/notes")
    public String processNotes(
            @ModelAttribute("noteRequest") InspectionRequest request,
            RedirectAttributes redirectAttributes) {

        // Gọi service xử lý (hoặc tận dụng hàm createInspection / update sẵn có)
        repairService.createInspection(request);

        redirectAttributes.addFlashAttribute("successMessage", "Đã cập nhật ghi chú kỹ thuật thành công!");
        return "redirect:/technician/assigned-jobs";
    }
    // Bổ sung vào class RepairController

    // 1. Hiển thị trang đề xuất sửa chữa
    @GetMapping("/technician/repair-proposal")
    public String showProposalForm(@RequestParam(required = false) Long vehicleId, Model model) {
        InspectionRequest request = new InspectionRequest();
        if (vehicleId != null) {
            request.setVehicleId(vehicleId);
        }
        model.addAttribute("proposalRequest", request);
        return "technician/repair-proposal"; // Trỏ đến templates/technician/repair-proposal.html
    }

    // 2. Xử lý submit đề xuất sửa chữa
    @PostMapping("/technician/repair-proposal")
    public String processProposal(
            @ModelAttribute("proposalRequest") InspectionRequest request,
            RedirectAttributes redirectAttributes) {

        // Gọi service xử lý phiếu kiểm tra / đề xuất
        repairService.createInspection(request);

        redirectAttributes.addFlashAttribute("successMessage", "Gửi đề xuất sửa chữa thành công!");
        return "redirect:/technician/assigned-jobs";
    }
    // Bổ sung vào class RepairController

    // 1. Hiển thị form cập nhật tiến độ
    @GetMapping("/technician/progress-update")
    public String showProgressForm(@RequestParam(required = false) Long vehicleId, Model model) {
        InspectionRequest request = new InspectionRequest();
        if (vehicleId != null) {
            request.setVehicleId(vehicleId);
        }
        request.setProgress(50); // Giá trị mặc định 50%
        model.addAttribute("progressRequest", request);
        return "technician/progress-update"; // Trỏ đến templates/technician/progress-update.html
    }

    // 2. Xử lý lưu tiến độ
    @PostMapping("/technician/progress-update")
    public String processProgressUpdate(
            @ModelAttribute("progressRequest") InspectionRequest request,
            RedirectAttributes redirectAttributes) {

        repairService.createInspection(request); // Hoặc gọi hàm update tiến độ trong service

        redirectAttributes.addFlashAttribute("successMessage", "Đã cập nhật tiến độ công việc thành công!");
        return "redirect:/technician/assigned-jobs";
    }
    // Bổ sung vào class RepairController

    // 1. Hiển thị trang Upload Ảnh
    @GetMapping("/technician/upload-images")
    public String showUploadImagesForm(@RequestParam(required = false) Long vehicleId, Model model) {
        InspectionRequest request = new InspectionRequest();
        if (vehicleId != null) {
            request.setVehicleId(vehicleId);
        }
        model.addAttribute("uploadRequest", request);
        return "technician/upload-images"; // Trỏ đến templates/technician/upload-images.html
    }

    // 2. Xử lý Upload Ảnh
    @PostMapping("/technician/upload-images")
    public String processUploadImages(
            @ModelAttribute("uploadRequest") InspectionRequest request,
            RedirectAttributes redirectAttributes) {

        // Ví dụ kiểm tra xem KTV có chọn ảnh không
        if (request.getImages() != null && !request.getImages().isEmpty()) {
            System.out.println("Đã nhận " + request.getImages().size() + " file ảnh cho vehicleId: " + request.getVehicleId());
            // Gọi service lưu file ảnh vào thư mục/database tại đây nếu cần
        }

        redirectAttributes.addFlashAttribute("successMessage", "Tải lên hình ảnh thành công!");
        return "redirect:/technician/assigned-jobs";
    }
    // Bổ sung vào class RepairController

    // 1. Hiển thị trang bàn giao xe / nghiệm thu
    @GetMapping("/technician/handover")
    public String showHandoverForm(@RequestParam(required = false) Long vehicleId, Model model) {
        InspectionRequest request = new InspectionRequest();
        if (vehicleId != null) {
            request.setVehicleId(vehicleId);
        }
        // Đặt mặc định tiến độ là 100% khi bàn giao
        request.setProgress(100);

        model.addAttribute("handoverRequest", request);
        return "technician/handover"; // Trỏ đến templates/technician/handover.html
    }

    // 2. Xử lý submit bàn giao
    @PostMapping("/technician/handover")
    public String processHandover(
            @ModelAttribute("handoverRequest") InspectionRequest request,
            RedirectAttributes redirectAttributes) {

        // Gọi service xử lý bàn giao / hoàn thành đơn sửa chữa
        repairService.createInspection(request);

        redirectAttributes.addFlashAttribute("successMessage", "Đã lập biên bản bàn giao & hoàn thành sửa chữa!");
        return "redirect:/technician/assigned-jobs";
    }
}