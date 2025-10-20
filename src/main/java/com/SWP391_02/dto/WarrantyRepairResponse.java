package com.SWP391_02.dto;

import com.SWP391_02.entity.WarrantyRepair;
import lombok.*;
import java.time.LocalDateTime;

@Getter @Setter @NoArgsConstructor @AllArgsConstructor @Builder
public class WarrantyRepairResponse {
    private Long id;
    private Long claimId;
    private Long technicianId;
    private String vin;
    private String description;
    private String partsUsed;
    private String status;
    private LocalDateTime repairDate;
    private LocalDateTime updatedAt;

    public WarrantyRepairResponse(WarrantyRepair r) {
        this.id = r.getId();
        this.claimId = r.getClaimId();
        this.technicianId = r.getTechnicianId();
        this.vin = r.getVin();
        this.description = r.getDescription();
        this.partsUsed = r.getPartsUsed();
        this.status = r.getStatus();
        this.repairDate = r.getRepairDate();
        this.updatedAt = r.getUpdatedAt();
    }
}
