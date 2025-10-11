package com.SWP391_02.repository;

import com.SWP391_02.entity.ClaimApproval;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;
import java.util.List;

@Repository
public interface ClaimApprovalRepository extends JpaRepository<ClaimApproval, Long> {

    List<ClaimApproval> findByClaimId(Long claimId);

    List<ClaimApproval> findByClaimIdOrderByDecisionAtDesc(Long claimId);
}
