package com.SWP391_02.service;

import com.SWP391_02.dto.CoverageDTO;
import com.SWP391_02.dto.HistoryEventDTO;
import com.SWP391_02.dto.WarrantyLookupResponse;
import com.SWP391_02.dto.WarrantyRepairHistoryDTO;
import com.SWP391_02.entity.WarrantyRepair;
import com.SWP391_02.repository.VehicleRepository;
import com.SWP391_02.repository.WarrantyCoverageRepository;
import com.SWP391_02.repository.WarrantyRepairRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;

import java.time.LocalDate;
import java.time.temporal.ChronoUnit;
import java.util.ArrayList;
import java.util.List;

@Service
@RequiredArgsConstructor
public class WarrantyService {

    private final VehicleRepository vehicleRepo;
    private final WarrantyCoverageRepository coverageRepo;
    private final WarrantyRepairRepository repairRepo; // ✅ thay vì eventRepo

    /**
     * Tra cứu trạng thái bảo hành tại mốc ngày asOf.
     * - Nếu không tìm thấy vehicle → thêm cảnh báo trong warnings.
     * - Lọc các coverage còn hiệu lực theo ngày (startDate <= asOf <= endDate).
     * - Sinh cảnh báo nếu bất kỳ coverage nào sắp hết hạn trong 30 ngày.
     */
    public WarrantyLookupResponse lookup(String vin, LocalDate asOf) {
        List<String> warnings = new ArrayList<>();

        var vehicleOpt = vehicleRepo.findById(vin);
        if (vehicleOpt.isEmpty()) {
            warnings.add("Không tìm thấy phương tiện với VIN " + vin);
        }

        var activeCoverages = coverageRepo
                .findByVinAndStartDateLessThanEqualAndEndDateGreaterThanEqual(vin, asOf, asOf)
                .stream()
                .map(c -> CoverageDTO.builder()
                        .partCategory(c.getPartCategory())
                        .startDate(c.getStartDate())
                        .endDate(c.getEndDate())
                        .mileageLimit(c.getMileageLimit())
                        .build())
                .toList();

        boolean active = !activeCoverages.isEmpty();

        // Cảnh báo: coverage sắp hết hạn (<= 30 ngày)
        coverageRepo.findByVin(vin).forEach(c -> {
            if (c.getEndDate() != null) {
                long days = ChronoUnit.DAYS.between(asOf, c.getEndDate());
                if (days >= 0 && days <= 30) {
                    warnings.add("Gói " + c.getPartCategory() + " sẽ hết hạn sau " + days + " ngày.");
                }
            }
        });

        return WarrantyLookupResponse.builder()
                .vin(vin)
                .asOfDate(asOf)
                .active(active)
                .activeCoverages(activeCoverages)
                .warnings(warnings)
                .build();
    }

    /**
     * Lấy lịch sử sửa chữa / bảo hành theo VIN (từ bảng warranty_repairs)
     */
    public Page<WarrantyRepairHistoryDTO> history(String vin, Pageable pageable) {
        return repairRepo.findByVinContainingIgnoreCase(vin, pageable)
                .map(this::toRepairHistoryDTO);
    }

    private WarrantyRepairHistoryDTO toRepairHistoryDTO(WarrantyRepair repair) {
        return WarrantyRepairHistoryDTO.builder()
                .description(repair.getDescription())
                .partsUsed(repair.getPartsUsed())
                .status(repair.getStatus())
                .repairDate(repair.getRepairDate() == null ? null : repair.getRepairDate().toString())
                .build();
    }

}
