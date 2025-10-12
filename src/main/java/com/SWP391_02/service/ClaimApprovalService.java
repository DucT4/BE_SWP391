package com.SWP391_02.service;

import com.SWP391_02.entity.Claim;
import com.SWP391_02.entity.ClaimApproval;
import com.SWP391_02.entity.User;
import com.SWP391_02.repository.ClaimApprovalRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
@RequiredArgsConstructor
public class ClaimApprovalService {

    private final ClaimApprovalRepository repo;

    @Transactional
    public void record(Claim claim, User approver, String level, String decision, String remark) {
        ClaimApproval approval = new ClaimApproval();
        approval.setClaim(claim);
        approval.setApprover(approver);
        approval.setLevel(level);
        approval.setDecision(decision);
        approval.setRemark(remark);
        repo.save(approval);
    }
}