package com.SWP391_02.dto;

import com.SWP391_02.entity.Claim;
import com.SWP391_02.enums.ApprovalLevel;
import com.SWP391_02.enums.ClaimStatus;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class ClaimResponse {

    private Long id;
    private String vin;
    private Long openedBy;
    private String openedByUserName;
    private Long serviceCenterId;
    private String serviceCenterName;
    private ClaimStatus status;
    private String failureDesc;
    private ApprovalLevel approvalLevel;
    private String resolutionType;
    private String resolutionNote;
    private LocalDateTime createdAt;
    private LocalDateTime updatedAt;

    // ✅ Constructor nhận entity Claim
    public ClaimResponse(Claim claim) {
        this.id = claim.getId();
        this.vin = claim.getVin();
        this.openedBy = claim.getOpenedBy();

        // Lấy username từ quan hệ ManyToOne (nếu có)
        if (claim.getOpenedByUser() != null) {
            this.openedByUserName = claim.getOpenedByUser().getUsername();
        }

        this.serviceCenterId = claim.getServiceCenterId();

        if (claim.getServiceCenter() != null) {
            this.serviceCenterName = claim.getServiceCenter().getName();
        }

        this.status = claim.getStatus();
        this.failureDesc = claim.getFailureDesc();
        this.approvalLevel = claim.getApprovalLevel();
        this.resolutionType = claim.getResolutionType();
        this.resolutionNote = claim.getResolutionNote();
        this.createdAt = claim.getCreatedAt();
        this.updatedAt = claim.getUpdatedAt();
    }
}
