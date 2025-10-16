package com.SWP391_02.service;

import com.SWP391_02.dto.InspectionReportRequest;
import com.SWP391_02.dto.InspectionReportResponse;
import com.SWP391_02.entity.*;
import com.SWP391_02.enums.ClaimStatus;
import com.SWP391_02.repository.*;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;

@Service
@RequiredArgsConstructor
public class InspectionReportService {

    private final InspectionReportRepository inspectionRepo;
    private final ClaimRepository claimRepo;
    private final UserRepository userRepo;

    @Transactional
    public InspectionReportResponse createReport(InspectionReportRequest req) {

        // 1️⃣ Kiểm tra claim tồn tại
        Claim claim = claimRepo.findById(req.getClaimId())
                .orElseThrow(() -> new RuntimeException("Claim not found"));

        // 2️⃣ Lấy technician và staff (nếu có)
        User technician = req.getTechnicianId() != null
                ? userRepo.findById(req.getTechnicianId()).orElse(null)
                : null;
        User staff = req.getStaffId() != null
                ? userRepo.findById(req.getStaffId()).orElse(null)
                : null;

        // 3️⃣ Chuẩn hóa list ảnh
        String imageString = (req.getImages() != null && !req.getImages().isEmpty())
                ? String.join(",", req.getImages())
                : null;

        // 4️⃣ Tạo mới report
        InspectionReport report = InspectionReport.builder()
                .claim(claim)
                .technician(technician)
                .staff(staff)
                .summary(req.getSummary())
                .findings(req.getFindings())
                .images(imageString)
                .status("REPORTED")
                .inspectionDate(LocalDateTime.now())
                .createdAt(LocalDateTime.now())
                .build();

        // 🔥 Save thật vào DB
        report = inspectionRepo.saveAndFlush(report);

        // 5️⃣ Update Claim status
        claim.setStatus(ClaimStatus.INSPECTED);
        claim.setUpdatedAt(LocalDateTime.now());
        claimRepo.save(claim);

        // 6️⃣ Trả về JSON thật
        return InspectionReportResponse.builder()
                .id(report.getId())
                .claimId(claim.getId())
                .summary(report.getSummary())
                .findings(report.getFindings())
                .images(report.getImages())
                .status(report.getStatus())
                .createdAt(report.getCreatedAt())
                .build();
    }
}
