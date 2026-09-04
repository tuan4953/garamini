package com.garage.sparepart.controller;

import com.garage.sparepart.dto.SparePartResponse;
import com.garage.sparepart.service.SparePartService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/spare-parts")
@RequiredArgsConstructor
public class SparePartController {

    private final SparePartService sparePartService;

    @GetMapping
    public ResponseEntity<List<SparePartResponse>> getAllActiveSpareParts() {
        return ResponseEntity.ok(sparePartService.getAllActiveSpareParts());
    }

    @GetMapping("/{id}")
    public ResponseEntity<SparePartResponse> getSparePartById(@PathVariable Long id) {
        return ResponseEntity.ok(sparePartService.getSparePartById(id));
    }
}