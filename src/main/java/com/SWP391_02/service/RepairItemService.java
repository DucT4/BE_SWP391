package com.SWP391_02.service;

import com.SWP391_02.entity.*;
import com.SWP391_02.enums.ClaimStatus;
import com.SWP391_02.repository.*;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;
import java.util.List;

@Service
@RequiredArgsConstructor
public class RepairItemService {

    private final RepairItemRepository repairRepo;
    private final ClaimRepository claimRepo;
    private final PartRepository partRepo;
    private final UserRepository userRepo;

    @Transactional
    public String confirmRepairItems(Long claimId, Long techId, List<RepairItem> items) {

        Claim claim = claimRepo.findById(claimId)
                .orElseThrow(() -> new RuntimeException("Claim not found"));
        User tech = userRepo.findById(techId)
                .orElseThrow(() -> new RuntimeException("Technician not found"));

        for (RepairItem item : items) {
            Part part = (item.getPart() != null && item.getPart().getId() != null)
                    ? partRepo.findById(item.getPart().getId()).orElse(null)
                    : null;

            RepairItem newItem = RepairItem.builder()
                    .claim(claim)
                    .part(part)
                    .description(item.getDescription())
                    .estimatedCost(item.getEstimatedCost())
                    .quantity(item.getQuantity())
                    .confirmedBy(tech)
                    .confirmedAt(LocalDateTime.now())
                    .build();

            repairRepo.save(newItem);
        }

        // Cập nhật trạng thái claim
        claim.setStatus(ClaimStatus.CONFIRMED_REPAIR);
        claim.setUpdatedAt(LocalDateTime.now());
        claimRepo.save(claim);

        return "✅ Repair items and cost confirmed successfully";
    }
}
