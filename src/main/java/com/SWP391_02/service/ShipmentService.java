package com.SWP391_02.service;

import com.SWP391_02.entity.*;
import com.SWP391_02.repository.*;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import java.time.LocalDate;
import java.util.List;

@Service
@RequiredArgsConstructor
public class ShipmentService {

    private final ShipmentRepository shipmentRepo;
    private final ShipmentLineRepository lineRepo;
    private final PartsAllocationRepository allocationRepo;
    private final PartRepository partRepo;

    public Shipment createShipment(Long allocationId, List<Long> partIds, List<Integer> qtys) {
        PartsAllocation allocation = allocationRepo.findById(allocationId)
                .orElseThrow(() -> new RuntimeException("Allocation not found"));

        Shipment shipment = Shipment.builder()
                .shipmentCode("SHIP-" + System.currentTimeMillis())
                .allocation(allocation)
                .fromWarehouse(allocation.getFromWarehouse())
                .toServiceCenter(allocation.getToServiceCenter())
                .status("CREATED")
                .shipDate(LocalDate.now())
                .build();

        Shipment savedShipment = shipmentRepo.save(shipment);

        for (int i = 0; i < partIds.size(); i++) {
            Part part = partRepo.findById(partIds.get(i))
                    .orElseThrow(() -> new RuntimeException("Part not found"));
            ShipmentLine line = ShipmentLine.builder()
                    .shipment(savedShipment)
                    .part(part)
                    .quantity(qtys.get(i))
                    .build();
            lineRepo.save(line);
        }

        allocation.setStatus("SHIPPED");
        allocationRepo.save(allocation);

        return savedShipment;
    }

    public List<Shipment> getAllShipments() {
        return shipmentRepo.findAll();
    }

    public Shipment markDelivered(Long shipmentId) {
        Shipment shipment = shipmentRepo.findById(shipmentId)
                .orElseThrow(() -> new RuntimeException("Shipment not found"));
        shipment.setStatus("DELIVERED");
        shipment.setDeliveredDate(LocalDate.now());
        return shipmentRepo.save(shipment);
    }
}