package com.SWP391_02.dto;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;

@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class ClaimAssignmentResponse {
    private Long id;
    private Long claimId;
    private Long technicianId;
    private String technicianName;
    private Long assignedBy;
    private String assignedByName;
    private String status;
    private LocalDateTime assignedAt;
    private LocalDateTime acceptedAt;
    private LocalDateTime startedAt;
    private LocalDateTime completedAt;
    private String note;
}

