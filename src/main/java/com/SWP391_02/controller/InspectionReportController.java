package com.SWP391_02.controller;

import com.SWP391_02.dto.InspectionReportRequest;
import com.SWP391_02.dto.InspectionReportResponse;
import com.SWP391_02.service.InspectionReportService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/inspection-reports")
@RequiredArgsConstructor
public class InspectionReportController {

    private final InspectionReportService inspectionReportService;

    @PostMapping("/create")
    public ResponseEntity<InspectionReportResponse> createInspectionReport(
            @RequestBody InspectionReportRequest request) {

        InspectionReportResponse response = inspectionReportService.createReport(request);
        System.out.println("✅ DEBUG Controller Response: " + response);

        ResponseEntity<InspectionReportResponse> entity = ResponseEntity.ok(response);
        System.out.println("✅ DEBUG Entity ready to return");
        return entity;
    }


}
