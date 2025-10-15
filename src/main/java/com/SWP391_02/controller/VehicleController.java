package com.SWP391_02.controller;

import com.SWP391_02.dto.CreateVehicleRequest;
import com.SWP391_02.entity.Vehicles;
import com.SWP391_02.service.VehicleService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.security.SecurityRequirement;
import lombok.RequiredArgsConstructor;
import org.springframework.http.*;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

import java.net.URI;

@RestController
@RequestMapping("/api/vehicles")
@RequiredArgsConstructor
@SecurityRequirement(name = "bearerAuth")
public class VehicleController {

    private final VehicleService service;

    @Operation(summary = "Tạo Vehicle mới")
    @PreAuthorize("hasAnyAuthority('EVM_ADMIN', 'EVM_STAFF')")
    @PostMapping
    public ResponseEntity<?> create(@RequestBody CreateVehicleRequest req) {
        Vehicles v = service.create(req);
        return ResponseEntity.created(URI.create("/api/vehicles/" + v.getVin())).body(v);
    }

    @Operation(summary = "Lấy chi tiết Vehicle theo VIN")
    @PreAuthorize("hasAnyAuthority('SC_TECHNICIAN', 'SC_MANAGER', 'EVM_STAFF', 'EVM_ADMIN')")
    @GetMapping("/{vin}")
    public ResponseEntity<Vehicles> get(@PathVariable String vin) {
        return ResponseEntity.ok(service.getByVin(vin));
    }

    @Operation(summary = "Xóa Vehicle")
    @PreAuthorize("hasAuthority('EVM_ADMIN')")
    @DeleteMapping("/{vin}")
    public ResponseEntity<Void> delete(@PathVariable String vin) {
        service.delete(vin);
        return ResponseEntity.noContent().build();
    }
}
