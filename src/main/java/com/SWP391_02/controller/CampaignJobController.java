package com.SWP391_02.controller;

import com.SWP391_02.dto.CampaignJobRequestDTO;
import com.SWP391_02.dto.CampaignJobResponseDTO;
import com.SWP391_02.enums.CampaignJobStatus;
import com.SWP391_02.service.CampaignJobService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import java.util.List;
import java.util.Optional;

@RestController
@RequestMapping("/api/campaign-jobs")
@RequiredArgsConstructor
public class CampaignJobController {
    private final CampaignJobService campaignJobService;

    // Lấy danh sách job cho technician theo trạng thái
    @GetMapping("/technician/{technicianId}")
    public ResponseEntity<List<CampaignJobResponseDTO>> getJobsForTechnician(
            @PathVariable Long technicianId,
            @RequestParam CampaignJobStatus status) {
        List<CampaignJobResponseDTO> dtos = campaignJobService.getJobsForTechnicianDTO(technicianId, status);
        return ResponseEntity.ok(dtos);
    }

    // Tech bắt đầu job
    @PostMapping("/{jobId}/start")
    public ResponseEntity<CampaignJobResponseDTO> startJob(
            @PathVariable Long jobId,
            @RequestParam Long technicianId) {
        return campaignJobService.startJobDTO(jobId, technicianId)
                .map(ResponseEntity::ok)
                .orElse(ResponseEntity.badRequest().build());
    }

    // Tech hoàn thành job
    @PostMapping("/{jobId}/complete")
    public ResponseEntity<CampaignJobResponseDTO> completeJob(
            @PathVariable Long jobId,
            @RequestParam Long technicianId,
            @RequestBody CampaignJobRequestDTO req) {
        return campaignJobService.completeJobDTO(
                jobId, technicianId,
                req.getInspectionNote(),
                req.getRepairDescription(),
                req.getPartsUsed(),
                req.getCostEstimate(),
                req.getCustomerConfirmed(),
                req.getNote()
        ).map(ResponseEntity::ok)
         .orElse(ResponseEntity.badRequest().build());
    }

    // Lấy chi tiết job
    @GetMapping("/{jobId}")
    public ResponseEntity<CampaignJobResponseDTO> getJobDetail(@PathVariable Long jobId) {
        return campaignJobService.getJobDetailDTO(jobId)
                .map(ResponseEntity::ok)
                .orElse(ResponseEntity.notFound().build());
    }
}
