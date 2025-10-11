package com.SWP391_02.service;

import com.SWP391_02.entity.Claim;
import com.SWP391_02.entity.ClaimStatusHistory;
import com.SWP391_02.entity.User;
import com.SWP391_02.repository.ClaimStatusHistoryRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
@RequiredArgsConstructor
public class ClaimStatusHistoryService {

    private final ClaimStatusHistoryRepository repo;

    @Transactional
    public void log(Claim claim, String status, User actor, String note) {
        ClaimStatusHistory history = new ClaimStatusHistory();
        history.setClaim(claim);
        history.setStatus(status);
        history.setChangedBy(actor);
        history.setNote(note);
        repo.save(history);
    }
}