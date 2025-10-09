package com.SWP391_02.service;

import com.SWP391_02.dto.CoverageDTO;
import com.SWP391_02.dto.HistoryEventDTO;
import com.SWP391_02.dto.WarrantyLookupResponse;
import com.SWP391_02.entity.WarrantyEvent;
import com.SWP391_02.repository.VehicleRepository;
import com.SWP391_02.repository.WarrantyCoverageRepository;
import com.SWP391_02.repository.WarrantyEventRepository;
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
    private final WarrantyEventRepository eventRepo;

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
     * Lấy lịch sử sự kiện theo VIN (phân trang), sắp xếp mới nhất trước.
     */
    public Page<HistoryEventDTO> history(String vin, Pageable pageable) {
        return eventRepo.findByVinOrderByEventTimeDesc(vin, pageable)
                .map(this::toHistoryDTO);
    }

    // ===== Helpers =====
    private HistoryEventDTO toHistoryDTO(WarrantyEvent e) {
        return new HistoryEventDTO(
                e.getType().name(),
                e.getReference(),
                e.getNote(),
                e.getEventTime() == null ? null : e.getEventTime().toString()
        );
    }
}
