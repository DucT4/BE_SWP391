package com.SWP391_02.controller;

import com.SWP391_02.service.WarrantyRepairService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.security.SecurityRequirement;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/warranty/repairs")
@RequiredArgsConstructor
@SecurityRequirement(name = "bearerAuth")
public class WarrantyRepairController {

    private final WarrantyRepairService service;

    @Operation(summary = "Technician thực hiện bảo hành (POST)")
    @PostMapping
    public ResponseEntity<?> performRepair(@RequestParam Long claimId,
                                           @RequestParam Long technicianId,
                                           @RequestParam String vin,
                                           @RequestParam(required = false) String description,
                                           @RequestParam(required = false) String partsUsed,
                                           @RequestParam(required = false) Double estimatedCost) {
        return service.performRepair(claimId, technicianId, vin, description, partsUsed, estimatedCost);
    }

    @Operation(summary = "Lấy chi tiết ca sửa (GET)")
    @GetMapping("/{id}")
    public ResponseEntity<?> getOne(@PathVariable Long id) {
        return service.getOne(id);
    }

    @Operation(summary = "Xóa ca sửa (DELETE)")
    @DeleteMapping("/{id}")
    public ResponseEntity<?> delete(@PathVariable Long id) {
        return service.delete(id);
    }
}
