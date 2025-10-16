package com.SWP391_02.service;


import com.SWP391_02.dto.CampaignJobResponseDTO;
import com.SWP391_02.entity.CampaignJob;
import com.SWP391_02.entity.User;
import com.SWP391_02.enums.CampaignJobStatus;
import com.SWP391_02.repository.CampaignJobRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;

@Service
public class CampaignJobService {
    @Autowired
    private CampaignJobRepository campaignJobRepository;

    // Lấy danh sách job cho technician theo trạng thái
    public List<CampaignJob> getJobsForTechnician(Long technicianId, CampaignJobStatus status) {
        User technician = new User();
        technician.setId(technicianId);
        return campaignJobRepository.findByTechnicianAndStatus(technician, status);
    }

    // Tech bắt đầu job (chuyển sang IN_PROGRESS, set startedAt)
    @Transactional
    public Optional<CampaignJob> startJob(Long jobId, Long technicianId) {
        Optional<CampaignJob> jobOpt = campaignJobRepository.findById(jobId);
        if (jobOpt.isPresent()) {
            CampaignJob job = jobOpt.get();
            if (job.getTechnician() != null && job.getTechnician().getId().equals(technicianId)
                && job.getStatus() == CampaignJobStatus.ASSIGNED) {
                job.setStatus(CampaignJobStatus.IN_PROGRESS);
                job.setStartedAt(LocalDateTime.now());
                campaignJobRepository.save(job);
                return Optional.of(job);
            }
        }
        return Optional.empty();
    }

    // Tech hoàn thành job (chuyển sang COMPLETED, set completedAt và các thông tin hoàn thành)
    @Transactional
    public Optional<CampaignJob> completeJob(Long jobId, Long technicianId, String inspectionNote, String repairDescription, String partsUsed, Double costEstimate, Boolean customerConfirmed, String note) {
        Optional<CampaignJob> jobOpt = campaignJobRepository.findById(jobId);
        if (jobOpt.isPresent()) {
            CampaignJob job = jobOpt.get();
            if (job.getTechnician() != null && job.getTechnician().getId().equals(technicianId)
                && job.getStatus() == CampaignJobStatus.IN_PROGRESS) {
                job.setStatus(CampaignJobStatus.COMPLETED);
                job.setCompletedAt(LocalDateTime.now());
                job.setInspectionNote(inspectionNote);
                job.setRepairDescription(repairDescription);
                job.setPartsUsed(partsUsed);
                job.setCostEstimate(costEstimate);
                job.setCustomerConfirmed(customerConfirmed);
                job.setNote(note);
                campaignJobRepository.save(job);
                return Optional.of(job);
            }
        }
        return Optional.empty();
    }

    // Lấy chi tiết job
    public Optional<CampaignJob> getJobDetail(Long jobId) {
        return campaignJobRepository.findById(jobId);
    }

    // Mapping entity -> DTO
    public CampaignJobResponseDTO toDTO(CampaignJob job) {
        return CampaignJobResponseDTO.builder()
                .id(job.getId())
                .status(job.getStatus())
                .assignedAt(job.getAssignedAt())
                .startedAt(job.getStartedAt())
                .completedAt(job.getCompletedAt())
                .inspectionNote(job.getInspectionNote())
                .repairDescription(job.getRepairDescription())
                .partsUsed(job.getPartsUsed())
                .customerConfirmed(job.getCustomerConfirmed())
                .costEstimate(job.getCostEstimate())
                .note(job.getNote())
                .campaignId(job.getCampaign() != null ? job.getCampaign().getId() : null)
                .campaignName(job.getCampaign() != null ? job.getCampaign().getName() : null)
                .vin(job.getVehicle() != null ? job.getVehicle().getVin() : null)
                .technicianId(job.getTechnician() != null ? job.getTechnician().getId() : null)
                .technicianName(job.getTechnician() != null ? job.getTechnician().getUsername() : null)
                .build();
    }

    // Service methods trả về DTO
    public List<CampaignJobResponseDTO> getJobsForTechnicianDTO(Long technicianId, CampaignJobStatus status) {
        return getJobsForTechnician(technicianId, status).stream().map(this::toDTO).toList();
    }

    public Optional<CampaignJobResponseDTO> startJobDTO(Long jobId, Long technicianId) {
        return startJob(jobId, technicianId).map(this::toDTO);
    }

    public Optional<CampaignJobResponseDTO> completeJobDTO(Long jobId, Long technicianId, String inspectionNote, String repairDescription, String partsUsed, Double costEstimate, Boolean customerConfirmed, String note) {
        return completeJob(jobId, technicianId, inspectionNote, repairDescription, partsUsed, costEstimate, customerConfirmed, note).map(this::toDTO);
    }

    public Optional<CampaignJobResponseDTO> getJobDetailDTO(Long jobId) {
        return getJobDetail(jobId).map(this::toDTO);
    }
}
