package com.SWP391_02.dto;

import lombok.Builder;
import lombok.Data;

import java.time.LocalDateTime;

@Data
@Builder
public class InspectionReportResponse {
    private Long id;
    private Long claimId;
    private String summary;
    private String findings;
    private String images;
    private String status;
    private LocalDateTime createdAt;
}
