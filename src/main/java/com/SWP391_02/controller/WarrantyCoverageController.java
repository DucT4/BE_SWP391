// src/main/java/com/SWP391_02/controller/WarrantyCoverageController.java
package com.SWP391_02.controller;

import com.SWP391_02.dto.CreateCoverageRequest;
import com.SWP391_02.entity.WarrantyCoverage;
import com.SWP391_02.repository.VehicleRepository;
import com.SWP391_02.repository.WarrantyCoverageRepository;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.security.SecurityRequirement;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.http.*;
import org.springframework.web.bind.annotation.*;

import java.net.URI;

@RestController
@RequestMapping("/api/warranty/coverage")
@RequiredArgsConstructor
@SecurityRequirement(name = "bearerAuth")
public class WarrantyCoverageController {

    private final WarrantyCoverageRepository coverageRepo;
    private final VehicleRepository vehicleRepo;

    @Operation(summary = "Tạo coverage (POST)")
    @PostMapping
    public ResponseEntity<?> create(@Valid @RequestBody CreateCoverageRequest req) {
        if (req.endDate().isBefore(req.startDate())) {
            return ResponseEntity.badRequest().body("endDate phải >= startDate");
        }
        if (vehicleRepo.findById(req.vin()).isEmpty()) {
            return ResponseEntity.unprocessableEntity().body("VIN không tồn tại");
        }
        WarrantyCoverage c = WarrantyCoverage.builder()
                .vin(req.vin())
                .partCategory(req.partCategory())
                .startDate(req.startDate())
                .endDate(req.endDate())
                .mileageLimit(req.mileageLimit())
                .mileageAtStart(req.mileageAtStart())
                .build();
        coverageRepo.save(c);
        return ResponseEntity.created(URI.create("/api/warranty/coverage/" + c.getId())).build();
    }

    @Operation(summary = "Lấy coverage theo id (GET)")
    @GetMapping("/{id}")
    public ResponseEntity<?> getOne(@PathVariable Long id) {
        return coverageRepo.findById(id)
                .<ResponseEntity<?>>map(ResponseEntity::ok)
                .orElseGet(() -> ResponseEntity.status(HttpStatus.NOT_FOUND).body("Not found"));
    }

    @Operation(summary = "Liệt kê coverage theo VIN (GET, phân trang)")
    @GetMapping
    public Page<WarrantyCoverage> listByVin(@RequestParam String vin,
                                            @RequestParam(defaultValue = "0") int page,
                                            @RequestParam(defaultValue = "20") int size) {
        return coverageRepo.findByVinOrderByEndDateDesc(vin, PageRequest.of(page, size));
    }

    @Operation(summary = "Xóa coverage (DELETE)")
    @DeleteMapping("/{id}")
    public ResponseEntity<?> delete(@PathVariable Long id) {
        if (!coverageRepo.existsById(id)) return ResponseEntity.notFound().build();
        coverageRepo.deleteById(id);
        return ResponseEntity.noContent().build();
    }
}

