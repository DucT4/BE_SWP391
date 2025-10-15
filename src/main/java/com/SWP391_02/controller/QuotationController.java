package com.SWP391_02.controller;

import com.SWP391_02.service.QuotationService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.security.SecurityRequirement;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/warranty/quotations")
@RequiredArgsConstructor
@SecurityRequirement(name = "bearerAuth")
public class QuotationController {

    private final QuotationService service;

    @Operation(summary = "Lấy danh sách tất cả báo giá (GET)")
    @PreAuthorize("hasAnyAuthority('EVM_ADMIN', 'EVM_STAFF', 'SC_MANAGER')")
    @GetMapping
    public ResponseEntity<?> getAll() {
        return service.getAll();
    }

    @Operation(summary = "Lấy chi tiết 1 báo giá (GET /{id})")
    @PreAuthorize("hasAnyAuthority('EVM_ADMIN', 'EVM_STAFF', 'SC_MANAGER', 'SC_TECHNICIAN')")
    @GetMapping("/{id}")
    public ResponseEntity<?> getOne(@PathVariable Long id) {
        return service.getOne(id);
    }

    @Operation(summary = "Cập nhật trạng thái báo giá (PUT /{id})")
    @PreAuthorize("hasAnyAuthority('EVM_ADMIN', 'EVM_STAFF')")
    @PutMapping("/{id}")
    public ResponseEntity<?> updateStatus(@PathVariable Long id,
                                          @RequestParam String status,
                                          @RequestParam(required = false) String note) {
        return service.updateStatus(id, status, note);
    }

    @Operation(summary = "Xóa báo giá (DELETE /{id})")
    @PreAuthorize("hasAuthority('EVM_ADMIN')")
    @DeleteMapping("/{id}")
    public ResponseEntity<?> delete(@PathVariable Long id) {
        return service.delete(id);
    }
}
