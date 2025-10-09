// src/main/java/com/SWP391_02/controller/WarrantyEventController.java
package com.SWP391_02.controller;

import com.SWP391_02.dto.CreateEventRequest;
import com.SWP391_02.dto.HistoryEventDTO;
import com.SWP391_02.entity.WarrantyEvent;
import com.SWP391_02.repository.VehicleRepository;
import com.SWP391_02.repository.WarrantyEventRepository;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.security.SecurityRequirement;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.*;
import org.springframework.http.*;
import org.springframework.web.bind.annotation.*;

import java.net.URI;

@RestController
@RequestMapping("/api/warranty/events")
@RequiredArgsConstructor
@SecurityRequirement(name = "bearerAuth")
public class WarrantyEventController {

    private final WarrantyEventRepository eventRepo;
    private final VehicleRepository vehicleRepo;

    @Operation(summary = "Tạo event lịch sử (POST)")
    @PostMapping
    public ResponseEntity<?> create(@Valid @RequestBody CreateEventRequest req) {
        if (vehicleRepo.findById(req.vin()).isEmpty()) {
            return ResponseEntity.unprocessableEntity().body("VIN không tồn tại");
        }
        WarrantyEvent e = WarrantyEvent.builder()
                .vin(req.vin())
                .type(req.type())
                .eventTime(req.eventTime())
                .reference(req.reference())
                .note(req.note())
                .build();
        eventRepo.save(e);
        return ResponseEntity.created(URI.create("/api/warranty/events/" + e.getId())).build();
    }

    @Operation(summary = "Danh sách event theo VIN (GET, phân trang)")
    @GetMapping
    public Page<HistoryEventDTO> list(@RequestParam String vin,
                                      @RequestParam(defaultValue = "0") int page,
                                      @RequestParam(defaultValue = "20") int size) {
        return eventRepo.findByVinOrderByEventTimeDesc(vin, PageRequest.of(page, size))
                .map(e -> new HistoryEventDTO(
                        e.getType().name(),
                        e.getReference(),
                        e.getNote(),
                        e.getEventTime() == null ? null : e.getEventTime().toString()
                ));
    }

    @Operation(summary = "Xóa event (DELETE)")
    @DeleteMapping("/{id}")
    public ResponseEntity<?> delete(@PathVariable Long id) {
        if (!eventRepo.existsById(id)) return ResponseEntity.notFound().build();
        eventRepo.deleteById(id);
        return ResponseEntity.noContent().build();
    }
}
