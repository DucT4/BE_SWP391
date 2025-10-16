package com.SWP391_02.service;

import com.SWP391_02.entity.PartsRequest;
import com.SWP391_02.entity.Part;
import com.SWP391_02.entity.ServiceCenters;
import com.SWP391_02.repository.PartsRequestRepository;
import com.SWP391_02.repository.PartRepository;
import com.SWP391_02.repository.ServiceCenterRepository;
import jakarta.transaction.Transactional;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.util.List;
@Transactional

@Service
@RequiredArgsConstructor
public class PartsRequestService {

    private final PartsRequestRepository requestRepo;
    private final PartRepository partRepo;
    private final ServiceCenterRepository scRepo;

    public PartsRequest createRequest(Long serviceCenterId, Long partId, Integer qty, String note) {
        Part part = partRepo.findById(partId)
                .orElseThrow(() -> new RuntimeException("Part not found"));
        ServiceCenters sc = scRepo.findById(serviceCenterId)
                .orElseThrow(() -> new RuntimeException("Service Center not found"));

        PartsRequest req = PartsRequest.builder()
                .requestCode("REQ-" + System.currentTimeMillis())
                .serviceCenter(sc)
                .part(part)
                .quantity(qty)
                .status("REQUESTED")
                .note(note)
                .build();

        return requestRepo.save(req);
    }

    public List<PartsRequest> getAllRequests() {
        return requestRepo.findAll();
    }

    public List<PartsRequest> getByServiceCenter(Long scId) {
        return requestRepo.findByServiceCenter_Id(scId);
    }

    public PartsRequest approveRequest(Long id) {
        PartsRequest req = requestRepo.findById(id)
                .orElseThrow(() -> new RuntimeException("Request not found"));
        req.setStatus("APPROVED");
        return requestRepo.save(req);
    }

    public PartsRequest rejectRequest(Long id, String note) {
        PartsRequest req = requestRepo.findById(id)
                .orElseThrow(() -> new RuntimeException("Request not found"));
        req.setStatus("REJECTED");
        req.setNote(note);
        return requestRepo.save(req);
    }
}
