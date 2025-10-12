package com.SWP391_02.service;

import com.SWP391_02.dto.CreateVehicleRequest;
import com.SWP391_02.entity.Vehicles;
import com.SWP391_02.repository.VehicleRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.dao.DataIntegrityViolationException;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;
import org.springframework.web.server.ResponseStatusException;

@Service
@RequiredArgsConstructor
public class VehicleService {

    private final VehicleRepository vehicleRepo;

    public Vehicles create(CreateVehicleRequest req) {
        if (vehicleRepo.existsById(req.vin())) {
            throw new ResponseStatusException(HttpStatus.CONFLICT, "VIN already exists");
        }

        Vehicles v = Vehicles.builder()
                .vin(req.vin())
                .model(req.model())
                .customerId(req.customerId())
                .purchaseDate(req.purchaseDate())
                .coverageTo(req.coverageTo())
                .build();

        return vehicleRepo.save(v);
    }

    public Vehicles getByVin(String vin) {
        return vehicleRepo.findById(vin)
                .orElseThrow(() -> new ResponseStatusException(HttpStatus.NOT_FOUND, "Vehicle not found"));
    }

    public void delete(String vin) {
        if (!vehicleRepo.existsById(vin)) {
            throw new ResponseStatusException(HttpStatus.NOT_FOUND, "VIN not found");
        }

        try {
            vehicleRepo.deleteById(vin);
        } catch (DataIntegrityViolationException e) {
            throw new ResponseStatusException(HttpStatus.CONFLICT,
                    "Không thể xóa: VIN đang được tham chiếu bởi dữ liệu khác (coverage/claim/event).");
        }
    }
}
