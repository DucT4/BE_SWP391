package com.SWP391_02.dto;

import com.SWP391_02.enums.CampaignJobStatus;
import lombok.Builder;
import lombok.Data;
import java.time.LocalDateTime;

@Data
@Builder
public class CampaignJobResponseDTO {
    private Long id;
    private CampaignJobStatus status;
    private LocalDateTime assignedAt;
    private LocalDateTime startedAt;
    private LocalDateTime completedAt;
    private String inspectionNote;
    private String repairDescription;
    private String partsUsed;
    private Boolean customerConfirmed;
    private Double costEstimate;
    private String note;
    private Long campaignId;
    private String campaignName;
    private String vin;
    private Long technicianId;
    private String technicianName;
}

