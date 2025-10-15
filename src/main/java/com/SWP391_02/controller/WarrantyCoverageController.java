package com.SWP391_02.controller;

import com.SWP391_02.dto.CreateCoverageRequest;
import com.SWP391_02.entity.WarrantyCoverage;
import com.SWP391_02.service.WarrantyCoverageService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.security.SecurityRequirement;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/warranty/coverage")
@RequiredArgsConstructor
@SecurityRequirement(name = "bearerAuth")
public class WarrantyCoverageController {

    private final WarrantyCoverageService service;

    @Operation(summary = "Tạo coverage (POST)")
    @PreAuthorize("hasAnyAuthority('ROLE_EVM_ADMIN', 'ROLE_EVM_STAFF')")
    @PostMapping
    public ResponseEntity<?> create(@Valid @RequestBody CreateCoverageRequest req) {
        return service.create(req);
    }

    @Operation(summary = "Lấy coverage theo id (GET)")
    @PreAuthorize("hasAnyAuthority('ROLE_SC_TECHNICIAN', 'ROLE_SC_MANAGER', 'ROLE_EVM_STAFF', 'ROLE_EVM_ADMIN')")
    @GetMapping("/{id}")
    public ResponseEntity<?> getOne(@PathVariable Long id) {
        return service.getOne(id);
    }

    @Operation(summary = "Liệt kê coverage theo VIN (GET, phân trang)")
    @PreAuthorize("hasAnyAuthority('ROLE_SC_TECHNICIAN', 'ROLE_SC_MANAGER', 'ROLE_EVM_STAFF', 'ROLE_EVM_ADMIN')")
    @GetMapping
    public Page<WarrantyCoverage> listByVin(@RequestParam String vin,
                                            @RequestParam(defaultValue = "0") int page,
                                            @RequestParam(defaultValue = "20") int size) {
        return service.listByVin(vin, page, size);
    }

    @Operation(summary = "Xóa coverage (DELETE)")
    @PreAuthorize("hasAuthority('ROLE_EVM_ADMIN')")
    @DeleteMapping("/{id}")
    public ResponseEntity<?> delete(@PathVariable Long id) {
        return service.delete(id);
    }
}
