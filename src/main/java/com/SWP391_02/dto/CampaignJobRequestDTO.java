package com.SWP391_02.dto;

import lombok.Data;

@Data
public class CampaignJobRequestDTO {
    private String inspectionNote;
    private String repairDescription;
    private String partsUsed;
    private Double costEstimate;
    private Boolean customerConfirmed;
    private String note;
}