package com.SWP391_02.controller;

import com.SWP391_02.entity.Part;
import com.SWP391_02.service.PartService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.security.SecurityRequirement;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@SecurityRequirement(name = "bearerAuth")
@RestController
@RequestMapping("/api/parts")
@RequiredArgsConstructor
public class PartController {

    private final PartService partService;

    @Operation(summary = "Lấy danh sách phụ tùng", description = "Trả về toàn bộ danh sách phụ tùng hiện có trong hệ thống.")
    @PreAuthorize("hasAnyAuthority('ROLE_EVM_ADMIN', 'ROLE_EVM_STAFF', 'ROLE_SC_MANAGER', 'ROLE_SC_TECHNICIAN')")
    @GetMapping
    public ResponseEntity<List<Part>> getAll() {
        return ResponseEntity.ok(partService.getAllParts());
    }

    @Operation(summary = "Lấy chi tiết phụ tùng", description = "Truy xuất thông tin chi tiết của 1 phụ tùng theo ID.")
    @PreAuthorize("hasAnyAuthority('ROLE_EVM_ADMIN', 'ROLE_EVM_STAFF', 'ROLE_SC_MANAGER', 'ROLE_SC_TECHNICIAN')")
    @GetMapping("/{id}")
    public ResponseEntity<Part> getById(@PathVariable Long id) {
        return partService.getPartById(id)
                .map(ResponseEntity::ok)
                .orElse(ResponseEntity.notFound().build());
    }

    @Operation(summary = "Tạo phụ tùng mới", description = "Thêm mới thông tin phụ tùng vào hệ thống.")
    @PreAuthorize("hasAnyAuthority('ROLE_EVM_ADMIN', 'ROLE_EVM_STAFF')")
    @PostMapping
    public ResponseEntity<Part> create(@RequestBody Part part) {
        return ResponseEntity.ok(partService.createPart(part));
    }

    @Operation(summary = "Cập nhật thông tin phụ tùng", description = "Chỉnh sửa dữ liệu phụ tùng theo ID.")
    @PreAuthorize("hasAnyAuthority('ROLE_EVM_ADMIN', 'ROLE_EVM_STAFF')")
    @PutMapping("/{id}")
    public ResponseEntity<Part> update(@PathVariable Long id, @RequestBody Part part) {
        return ResponseEntity.ok(partService.updatePart(id, part));
    }

    @Operation(summary = "Xóa phụ tùng", description = "Xóa phụ tùng khỏi hệ thống theo ID.")
    @PreAuthorize("hasAuthority('ROLE_EVM_ADMIN')")
    @DeleteMapping("/{id}")
    public ResponseEntity<Void> delete(@PathVariable Long id) {
        partService.deletePart(id);
        return ResponseEntity.noContent().build();
    }
}
