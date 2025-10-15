package com.SWP391_02.dto;

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
}
