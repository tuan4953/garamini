package com.garage.repair.dto;

import jakarta.validation.constraints.NotNull;
import lombok.*;
import org.springframework.web.multipart.MultipartFile;

import java.util.List;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class InspectionRequest {

    @NotNull(message = "Chưa chọn xe kiểm tra")
    private Long vehicleId;

    private Long technicianId;
    private String initialCondition;
    private String diagnosticResult;
    private Integer progress;
    private List<MultipartFile> images;
}