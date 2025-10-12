package com.SWP391_02.service;
//EVM cấp phát phụ tùng từ kho cho TT.
import com.SWP391_02.entity.*;
import com.SWP391_02.repository.*;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import java.time.LocalDate;
import java.util.List;

@Service
@RequiredArgsConstructor
public class PartsAllocationService {

    private final PartsAllocationRepository allocationRepo;
    private final PartsRequestRepository requestRepo;
    private final WarehouseRepository warehouseRepo;
    private final ServiceCenterRepository scRepo;

    public PartsAllocation allocateParts(Long requestId, Long warehouseId, Long scId) {
        PartsRequest req = requestRepo.findById(requestId)
                .orElseThrow(() -> new RuntimeException("Request not found"));
        Warehouse wh = warehouseRepo.findById(warehouseId)
                .orElseThrow(() -> new RuntimeException("Warehouse not found"));
        ServiceCenter sc = scRepo.findById(scId)
                .orElseThrow(() -> new RuntimeException("Service Center not found"));

        PartsAllocation allocation = PartsAllocation.builder()
                .allocationCode("ALLOC-" + System.currentTimeMillis())
                .request(req)
                .fromWarehouse(wh)
                .toServiceCenter(sc)
                .status("ALLOCATED")
                .etaDate(LocalDate.now().plusDays(3))
                .build();

        req.setStatus("ALLOCATED");
        requestRepo.save(req);

        return allocationRepo.save(allocation);
    }

    public List<PartsAllocation> getAll() {
        return allocationRepo.findAll();
    }

    public PartsAllocation updateStatus(Long id, String status) {
        PartsAllocation alloc = allocationRepo.findById(id)
                .orElseThrow(() -> new RuntimeException("Allocation not found"));
        alloc.setStatus(status);
        return allocationRepo.save(alloc);
    }
}
