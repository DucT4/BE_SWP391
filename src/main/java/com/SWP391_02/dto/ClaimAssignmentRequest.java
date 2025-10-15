package com.SWP391_02.dto;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class ClaimAssignmentRequest {
    private Long claimId;
    private Long technicianId;
    private String note;
}

