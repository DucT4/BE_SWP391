package com.SWP391_02.controller;

import com.SWP391_02.dto.CreateEventRequest;
import com.SWP391_02.dto.HistoryEventDTO;
import com.SWP391_02.service.WarrantyEventService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.security.SecurityRequirement;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/warranty/events")
@RequiredArgsConstructor
@SecurityRequirement(name = "bearerAuth")
public class WarrantyEventController {

    private final WarrantyEventService service;

    @Operation(summary = "Tạo event lịch sử (POST)")
    @PreAuthorize("hasAnyAuthority('ROLE_SC_TECHNICIAN', 'ROLE_EVM_STAFF', 'ROLE_EVM_ADMIN')")
    @PostMapping
    public ResponseEntity<?> create(@Valid @RequestBody CreateEventRequest req) {
        return service.create(req);
    }

    @Operation(summary = "Lấy chi tiết 1 event (GET /{id})")
    @PreAuthorize("hasAnyAuthority('ROLE_SC_TECHNICIAN', 'ROLE_SC_MANAGER', 'ROLE_EVM_STAFF', 'ROLE_EVM_ADMIN')")
    @GetMapping("/{id}")
    public ResponseEntity<?> getOne(@PathVariable Long id) {
        return service.getOne(id);
    }

    @Operation(summary = "Danh sách event theo VIN (GET, phân trang)")
    @PreAuthorize("hasAnyAuthority('ROLE_SC_TECHNICIAN', 'ROLE_SC_MANAGER', 'ROLE_EVM_STAFF', 'ROLE_EVM_ADMIN')")
    @GetMapping
    public Page<HistoryEventDTO> list(@RequestParam String vin,
                                      @RequestParam(defaultValue = "0") int page,
                                      @RequestParam(defaultValue = "20") int size) {
        return service.list(vin, page, size);
    }

    @Operation(summary = "Cập nhật event (PUT)")
    @PreAuthorize("hasAnyAuthority('ROLE_EVM_STAFF', 'ROLE_EVM_ADMIN')")
    @PutMapping("/{id}")
    public ResponseEntity<?> update(@PathVariable Long id,
                                    @Valid @RequestBody CreateEventRequest req) {
        return service.update(id, req);
    }

    @Operation(summary = "Xóa event (DELETE)")
    @PreAuthorize("hasAuthority('ROLE_EVM_ADMIN')")
    @DeleteMapping("/{id}")
    public ResponseEntity<?> delete(@PathVariable Long id) {
        return service.delete(id);
    }
}
