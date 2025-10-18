package com.SWP391_02.service;

import com.SWP391_02.entity.*;
import com.SWP391_02.repository.*;
import jakarta.transaction.Transactional;
import lombok.RequiredArgsConstructor;
import org.springframework.http.*;
import org.springframework.stereotype.Service;
import org.springframework.http.ResponseEntity;
import org.springframework.http.HttpStatus;

import java.time.LocalDateTime;

@Service
@RequiredArgsConstructor
@Transactional
public class WarrantyRepairService {

    private final WarrantyRepairRepository repairRepo;
    private final QuotationRepository quotationRepo;
    private final VehicleRepository vehicleRepo;

    /**
     * Kỹ thuật viên thực hiện bảo hành
     */
    public ResponseEntity<?> performRepair(Long claimId, Long technicianId, String vin,
                                           String description, String partsUsed, Double estimatedCost) {

        if (vehicleRepo.findById(vin).isEmpty()) {
            return ResponseEntity.unprocessableEntity().body("VIN không tồn tại");
        }

        WarrantyRepair repair = WarrantyRepair.builder()
                .claimId(claimId)
                .technicianId(technicianId)
                .vin(vin)
                .description(description)
                .partsUsed(partsUsed)
                .repairDate(LocalDateTime.now())
                .status("COMPLETED")
                .build();

        repairRepo.save(repair);

        // Nếu có chi phí (xe hết hạn bảo hành) -> tạo báo giá
        if (estimatedCost != null && estimatedCost > 0) {
            Quotation q = Quotation.builder()
                    .repairId(repair.getId())
                    .totalAmount(estimatedCost)
                    .note("Xe hết hạn bảo hành - phát sinh chi phí.")
                    .status("PENDING")
                    .createdAt(LocalDateTime.now())
                    .build();
            quotationRepo.save(q);
        }

        return ResponseEntity.ok("Repair completed successfully");
    }

    /**
     * Xem chi tiết 1 ca sửa
     */
    public ResponseEntity<?> getOne(Long id) {
        return repairRepo.findById(id)
                .<ResponseEntity<?>>map(ResponseEntity::ok)
                .orElseGet(() -> ResponseEntity.status(HttpStatus.NOT_FOUND).body("Không tìm thấy repair"));
    }

    /**
     * Xóa ca sửa
     */
    public ResponseEntity<?> delete(Long id) {
        if (!repairRepo.existsById(id)) {
            return ResponseEntity.notFound().build();
        }
        repairRepo.deleteById(id);
        return ResponseEntity.noContent().build();
    }

    public ResponseEntity<?> updateRepairStatus(Long id, String status, String remark) {
        var optional = repairRepo.findById(id);
        if (optional.isEmpty()) {
            return ResponseEntity.status(HttpStatus.NOT_FOUND).body("Repair record not found");
        }

        WarrantyRepair repair = optional.get();
        repair.setStatus(status != null ? status.toUpperCase() : null);
        if (remark != null && !remark.isBlank()) {
            repair.setDescription(
                    (repair.getDescription() != null ? repair.getDescription() + " | " : "") + remark
            );
        }
        repair.setUpdatedAt(LocalDateTime.now());
        repairRepo.save(repair);
        return ResponseEntity.ok(repair);
    }


}
