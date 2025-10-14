package com.SWP391_02.service;

import com.SWP391_02.dto.CreateCoverageRequest;
import com.SWP391_02.entity.WarrantyCoverage;
import com.SWP391_02.repository.VehicleRepository;
import com.SWP391_02.repository.WarrantyCoverageRepository;
import jakarta.transaction.Transactional;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;

import java.net.URI;

@Service
@RequiredArgsConstructor
@Transactional
public class WarrantyCoverageService {

    private final WarrantyCoverageRepository coverageRepo;
    private final VehicleRepository vehicleRepo;

    /**
     * Tạo mới một coverage
     */
    public ResponseEntity<?> create(CreateCoverageRequest req) {
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

    /**
     * Lấy coverage theo ID
     */
    public ResponseEntity<?> getOne(Long id) {
        return coverageRepo.findById(id)
                .<ResponseEntity<?>>map(ResponseEntity::ok)
                .orElseGet(() -> ResponseEntity.status(HttpStatus.NOT_FOUND).body("Not found"));
    }

    /**
     * Liệt kê các coverage theo VIN, có phân trang
     */
    public Page<WarrantyCoverage> listByVin(String vin, int page, int size) {
        return coverageRepo.findByVinOrderByEndDateDesc(vin, PageRequest.of(page, size));
    }

    /**
     * Xóa coverage theo ID
     */
    public ResponseEntity<?> delete(Long id) {
        if (!coverageRepo.existsById(id)) {
            return ResponseEntity.notFound().build();
        }
        coverageRepo.deleteById(id);
        return ResponseEntity.noContent().build();
    }
}
