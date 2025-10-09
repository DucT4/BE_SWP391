package com.SWP391_02.controller;
import com.SWP391_02.dto.CreateVehicleRequest;
import com.SWP391_02.entity.Vehicle;
import com.SWP391_02.repository.VehicleRepository;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.security.SecurityRequirement;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.dao.DataIntegrityViolationException;
import org.springframework.http.*;
import org.springframework.web.bind.annotation.*;

import java.net.URI;

@RestController
@RequestMapping("/api/vehicles")
@RequiredArgsConstructor
@SecurityRequirement(name = "bearerAuth")
public class VehicleController {

    private final VehicleRepository vehicleRepo;

    @Operation(summary = "Tạo Vehicle")
    @PostMapping
    public ResponseEntity<?> create(@Valid @RequestBody CreateVehicleRequest req) {
        if (vehicleRepo.existsById(req.vin())) {
            return ResponseEntity.status(HttpStatus.CONFLICT).body("VIN already exists");
        }
        Vehicle v = Vehicle.builder()
                .vin(req.vin())
                .model(req.model())
                .year(req.year())
                .ownerName(req.ownerName())
                .build();
        vehicleRepo.save(v);
        return ResponseEntity.created(URI.create("/api/vehicles/" + v.getVin())).build();
    }

    @Operation(summary = "Lấy chi tiết Vehicle theo VIN")
    @GetMapping("/{vin}")
    public ResponseEntity<?> get(@PathVariable String vin) {
        return vehicleRepo.findById(vin)
                .<ResponseEntity<?>>map(ResponseEntity::ok)
                .orElseGet(() -> ResponseEntity.status(HttpStatus.NOT_FOUND).body("Not found"));
    }

    @Operation(summary = "Xóa Vehicle")
    @DeleteMapping("/{vin}")
    public ResponseEntity<?> delete(@PathVariable String vin) {
        if (!vehicleRepo.existsById(vin)) {
            return ResponseEntity.notFound().build();
        }
        try {
            vehicleRepo.deleteById(vin);
            return ResponseEntity.noContent().build();
        } catch (DataIntegrityViolationException ex) {
            // Trường hợp có coverage/claim/event đang tham chiếu VIN này
            return ResponseEntity.status(HttpStatus.CONFLICT)
                    .body("Không thể xóa: VIN đang được tham chiếu bởi dữ liệu khác (coverage/claim/event).");
        }
    }
}
