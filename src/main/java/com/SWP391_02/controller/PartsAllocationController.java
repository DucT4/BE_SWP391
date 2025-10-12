package com.SWP391_02.controller;

import com.SWP391_02.entity.PartsAllocation;
import com.SWP391_02.service.PartsAllocationService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.security.SecurityRequirement;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import java.util.List;

@SecurityRequirement(name = "bearerAuth")
@RestController
@RequestMapping("/api/allocations")
@RequiredArgsConstructor
public class PartsAllocationController {

    private final PartsAllocationService service;

    @Operation(summary = "Phân bổ phụ tùng", description = "EVM Staff cấp phát phụ tùng từ kho đến Service Center.")
    @PostMapping
    public ResponseEntity<PartsAllocation> allocateParts(
            @RequestParam Long requestId,
            @RequestParam Long warehouseId,
            @RequestParam Long serviceCenterId) {
        return ResponseEntity.ok(service.allocateParts(requestId, warehouseId, serviceCenterId));
    }

    @Operation(summary = "Lấy danh sách phân bổ", description = "Hiển thị toàn bộ các phân bổ phụ tùng hiện tại.")
    @GetMapping
    public ResponseEntity<List<PartsAllocation>> getAll() {
        return ResponseEntity.ok(service.getAll());
    }

    @Operation(summary = "Cập nhật trạng thái phân bổ", description = "Thay đổi trạng thái phân bổ (ALLOCATED / SHIPPED / COMPLETED).")
    @PutMapping("/{id}/status")
    public ResponseEntity<PartsAllocation> updateStatus(@PathVariable Long id, @RequestParam String status) {
        return ResponseEntity.ok(service.updateStatus(id, status));
    }
}
