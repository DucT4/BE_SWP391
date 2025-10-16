package com.SWP391_02.dto;

import lombok.Data;
import java.util.List;

@Data
public class InspectionReportRequest {
    private Long claimId;
    private Long technicianId;
    private Long staffId;
    private String summary;
    private String findings;
    private List<String> images; // ✅ Đổi từ String sang List<String>
}
