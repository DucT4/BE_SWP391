package com.SWP391_02.controller;

import com.SWP391_02.dto.AddPartRequest;
import com.SWP391_02.dto.MessageResponse;
import com.SWP391_02.dto.PartResponse;
import com.SWP391_02.dto.PartUpdateRequest;
import com.SWP391_02.service.TechnicianProductService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.security.SecurityRequirement;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

@SecurityRequirement(name = "bearerAuth")
@RestController
@RequestMapping("/api/tech/products")
@RequiredArgsConstructor
public class TechnicianProductController {

    private final TechnicianProductService service;

    @Operation(
            summary = "Lấy danh sách sản phẩm (GET)",
            description = "Truy vấn danh sách phụ tùng có phân trang, tìm kiếm, sắp xếp.<br>"

    )
    @PreAuthorize("hasAnyAuthority('ROLE_SC_TECHNICIAN', 'ROLE_SC_MANAGER', 'ROLE_EVM_STAFF', 'ROLE_EVM_ADMIN')")
    @GetMapping
    public ResponseEntity<Page<PartResponse>> getAll(
            @RequestParam(required = false) String q,
            @RequestParam(defaultValue = "0") int page,
            @RequestParam(defaultValue = "20") int size,
            @RequestParam(required = false) String sortBy,
            @RequestParam(defaultValue = "desc") String dir
    ) {
        return ResponseEntity.ok(service.getAll(q, page, size, sortBy, dir));
    }


    @Operation(
            summary = "Cập nhật sản phẩm (PUT /{id})",
            description = "Chỉnh sửa thông tin phụ tùng hiện có.<br>"

    )
    @PreAuthorize("hasAnyAuthority('ROLE_EVM_ADMIN', 'ROLE_EVM_STAFF')")
    @PutMapping("/{id}")
    public ResponseEntity<PartResponse> update(
            @PathVariable Long id,
            @RequestBody @Valid PartUpdateRequest req
    ) {
        return ResponseEntity.ok(service.update(id, req));
    }
    @Operation(
            summary = "Xóa sản phẩm (DELETE /{id})",
            description = "Xóa phụ tùng khỏi hệ thống.<br>"

    )
    @PreAuthorize("hasAuthority('ROLE_EVM_ADMIN')")
    @DeleteMapping("/{id}")
    public ResponseEntity<Void> delete(@PathVariable Long id) {
        service.delete(id);
        return ResponseEntity.noContent().build();
    }

    @Operation(
            summary = "Thêm mới sản phẩm (POST)",
            description = "Tạo mới phụ tùng vào hệ thống.<br>"
    )
    @PreAuthorize("hasAnyAuthority('ROLE_EVM_ADMIN', 'ROLE_EVM_STAFF')")
    @PostMapping
    public ResponseEntity<?> addPart(@Valid @RequestBody AddPartRequest request) {
        try {
            PartResponse partResponse = service.create(request);
            return ResponseEntity.status(HttpStatus.CREATED).body(partResponse);
        } catch (RuntimeException e) {
            return ResponseEntity.badRequest()
                    .body(new MessageResponse("Error: " + e.getMessage()));
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                    .body(new MessageResponse("Error: An unexpected error occurred"));
        }
    }
}
