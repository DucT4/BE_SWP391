package com.SWP391_02.repository;

import com.SWP391_02.entity.InspectionReport;
import org.springframework.data.jpa.repository.JpaRepository;
import java.util.List;

public interface InspectionReportRepository extends JpaRepository<InspectionReport, Long> {
    List<InspectionReport> findByClaimId(Long claimId);
}
