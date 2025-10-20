package com.SWP391_02.controller;

import com.SWP391_02.dto.WarrantyRepairResponse;
import com.SWP391_02.service.WarrantyRepairService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.security.SecurityRequirement;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/warranty/repairs")
@RequiredArgsConstructor
@SecurityRequirement(name = "bearerAuth")
public class WarrantyRepairController {
    private final WarrantyRepairService warrantyRepairService;
    private final WarrantyRepairService service;

    @Operation(summary = "Technician thực hiện bảo hành (POST)")
    @PreAuthorize("hasAuthority('ROLE_SC_TECHNICIAN')")
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
    @PreAuthorize("hasAnyAuthority('ROLE_SC_MANAGER', 'ROLE_EVM_STAFF', 'ROLE_EVM_ADMIN', 'ROLE_SC_TECHNICIAN')")
    @GetMapping("/{id}")
    public ResponseEntity<?> getOne(@PathVariable Long id) {
        return service.getOne(id);
    }

    @Operation(summary = "Cập nhật trạng thái sửa chữa (PUT /{id}/status)")
    @PreAuthorize("hasAnyAuthority('ROLE_SC_TECHNICIAN', 'ROLE_EVM_STAFF', 'ROLE_EVM_ADMIN')")
    @PutMapping("/{id}/status")
    public ResponseEntity<?> updateStatus(@PathVariable Long id,
                                          @RequestParam String status,
                                          @RequestParam(required = false) String remark) {
        return service.updateRepairStatus(id, status, remark);
    }

    @Operation(summary = "Xóa ca sửa (DELETE)")
    @PreAuthorize("hasAuthority('ROLE_EVM_ADMIN')")
    @DeleteMapping("/{id}")
    public ResponseEntity<?> delete(@PathVariable Long id) {
        return service.delete(id);
    }
    @Operation(summary = "Lấy danh sách công việc bảo hành theo Technician")
    @PreAuthorize("hasAuthority('ROLE_SC_TECHNICIAN')")
    @GetMapping("/technician/{technicianId}")
    public ResponseEntity<?> getRepairsByTechnician(@PathVariable Long technicianId) {
        return service.getRepairsByTechnician(technicianId);
    }
    @GetMapping("/manager/{serviceCenterId}")
    @PreAuthorize("hasAuthority('ROLE_SC_MANAGER')")
    public ResponseEntity<List<WarrantyRepairResponse>> getRepairsByServiceCenter(
            @PathVariable Long serviceCenterId) {
        List<WarrantyRepairResponse> list = warrantyRepairService.getRepairsByServiceCenter(serviceCenterId);
        return ResponseEntity.ok(list);
    }




}
