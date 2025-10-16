package com.SWP391_02.repository;

import com.SWP391_02.entity.ClaimAssignment;
import com.SWP391_02.enums.ClaimAssignmentStatus;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
public interface ClaimAssignmentRepository extends JpaRepository<ClaimAssignment, Long> {
    List<ClaimAssignment> findByClaimId(Long claimId);
    List<ClaimAssignment> findByTechnicianId(Long technicianId);
    List<ClaimAssignment> findByTechnicianIdAndStatus(Long technicianId, ClaimAssignmentStatus status);
    Optional<ClaimAssignment> findByClaimIdAndStatus(Long claimId, ClaimAssignmentStatus status);
}
