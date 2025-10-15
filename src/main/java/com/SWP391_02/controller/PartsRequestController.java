package com.SWP391_02.controller;

import com.SWP391_02.entity.PartsRequest;
import com.SWP391_02.service.PartsRequestService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.security.SecurityRequirement;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@SecurityRequirement(name = "bearerAuth")
@RestController
@RequestMapping("/api/requests")
@RequiredArgsConstructor
public class PartsRequestController {

    private final PartsRequestService service;

    @Operation(summary = "Tạo yêu cầu phụ tùng", description = "Service Center gửi yêu cầu phụ tùng tới EVM Staff để xét duyệt.")
    @PreAuthorize("hasAnyAuthority('ROLE_SC_MANAGER', 'ROLE_SC_TECHNICIAN')")
    @PostMapping
    public ResponseEntity<PartsRequest> createRequest(
            @RequestParam Long serviceCenterId,
            @RequestParam Long partId,
            @RequestParam Integer quantity,
            @RequestParam(required = false) String note) {
        return ResponseEntity.ok(service.createRequest(serviceCenterId, partId, quantity, note));
    }

    @Operation(summary = "Lấy danh sách yêu cầu", description = "Lấy toàn bộ yêu cầu phụ tùng (chỉ EVM xem được).")
    @PreAuthorize("hasAnyAuthority('ROLE_EVM_ADMIN', 'ROLE_EVM_STAFF')")
    @GetMapping
    public ResponseEntity<List<PartsRequest>> getAllRequests() {
        return ResponseEntity.ok(service.getAllRequests());
    }

    @Operation(summary = "Lấy yêu cầu theo Service Center", description = "Hiển thị danh sách yêu cầu của 1 trung tâm dịch vụ cụ thể.")
    @PreAuthorize("hasAnyAuthority('ROLE_SC_MANAGER', 'ROLE_SC_TECHNICIAN', 'ROLE_EVM_ADMIN', 'ROLE_EVM_STAFF')")
    @GetMapping("/center/{scId}")
    public ResponseEntity<List<PartsRequest>> getByServiceCenter(@PathVariable Long scId) {
        return ResponseEntity.ok(service.getByServiceCenter(scId));
    }

    @Operation(summary = "Phê duyệt yêu cầu", description = "EVM Staff duyệt yêu cầu phụ tùng.")
    @PreAuthorize("hasAnyAuthority('ROLE_EVM_STAFF', 'ROLE_EVM_ADMIN')")
    @PutMapping("/{id}/approve")
    public ResponseEntity<PartsRequest> approveRequest(@PathVariable Long id) {
        return ResponseEntity.ok(service.approveRequest(id));
    }

    @Operation(summary = "Từ chối yêu cầu", description = "EVM Staff từ chối yêu cầu phụ tùng.")
    @PreAuthorize("hasAnyAuthority('ROLE_EVM_STAFF', 'ROLE_EVM_ADMIN')")
    @PutMapping("/{id}/reject")
    public ResponseEntity<PartsRequest> rejectRequest(@PathVariable Long id, @RequestParam String note) {
        return ResponseEntity.ok(service.rejectRequest(id, note));
    }
}
