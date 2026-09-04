package com.garage.rescue.controller;

import com.garage.rescue.dto.RescueRequestDto;
import com.garage.rescue.dto.RescueResponseDto;
import com.garage.rescue.service.RescueService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/rescue")
@RequiredArgsConstructor
public class RescueController {

    private final RescueService rescueService;

    @PostMapping("/request")
    public ResponseEntity<RescueResponseDto> requestRescue(@Valid @RequestBody RescueRequestDto dto) {
        return ResponseEntity.ok(rescueService.createRescueRequest(dto));
    }

    @GetMapping("/{id}")
    public ResponseEntity<RescueResponseDto> getRequestStatus(@PathVariable Long id) {
        return ResponseEntity.ok(rescueService.getRequestById(id));
    }
}