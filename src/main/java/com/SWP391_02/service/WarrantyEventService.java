package com.SWP391_02.service;

import com.SWP391_02.dto.CreateEventRequest;
import com.SWP391_02.dto.HistoryEventDTO;
import com.SWP391_02.entity.WarrantyEvent;
import com.SWP391_02.repository.VehicleRepository;
import com.SWP391_02.repository.WarrantyEventRepository;
import jakarta.transaction.Transactional;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.*;
import org.springframework.http.*;
import org.springframework.stereotype.Service;

import java.net.URI;
import java.util.Optional;

@Service
@RequiredArgsConstructor
@Transactional
public class WarrantyEventService {

    private final WarrantyEventRepository eventRepo;
    private final VehicleRepository vehicleRepo;

    /**
     * Tạo event lịch sử bảo hành mới
     */
    public ResponseEntity<?> create(CreateEventRequest req) {
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

    /**
     * Lấy thông tin chi tiết 1 event theo ID
     */
    public ResponseEntity<?> getOne(Long id) {
        Optional<WarrantyEvent> opt = eventRepo.findById(id);
        return opt.<ResponseEntity<?>>map(ResponseEntity::ok)
                .orElseGet(() -> ResponseEntity.status(HttpStatus.NOT_FOUND).body("Event không tồn tại"));
    }

    /**
     * Liệt kê danh sách event theo VIN (phân trang)
     */
    public Page<HistoryEventDTO> list(String vin, int page, int size) {
        return eventRepo.findByVinOrderByEventTimeDesc(vin, PageRequest.of(page, size))
                .map(e -> new HistoryEventDTO(
                        e.getType().name(),
                        e.getReference(),
                        e.getNote(),
                        e.getEventTime() == null ? null : e.getEventTime().toString()
                ));
    }

    /**
     * Cập nhật event
     */
    public ResponseEntity<?> update(Long id, CreateEventRequest req) {
        Optional<WarrantyEvent> optionalEvent = eventRepo.findById(id);
        if (optionalEvent.isEmpty()) {
            return ResponseEntity.status(HttpStatus.NOT_FOUND).body("Event không tồn tại");
        }

        WarrantyEvent e = optionalEvent.get();

        // Nếu VIN thay đổi, kiểm tra lại VIN hợp lệ
        if (!e.getVin().equals(req.vin()) && vehicleRepo.findById(req.vin()).isEmpty()) {
            return ResponseEntity.unprocessableEntity().body("VIN không tồn tại");
        }

        e.setVin(req.vin());
        e.setType(req.type());
        e.setEventTime(req.eventTime());
        e.setReference(req.reference());
        e.setNote(req.note());

        eventRepo.save(e);
        return ResponseEntity.ok(e);
    }

    /**
     * Xóa event theo ID
     */
    public ResponseEntity<?> delete(Long id) {
        if (!eventRepo.existsById(id)) {
            return ResponseEntity.notFound().build();
        }
        eventRepo.deleteById(id);
        return ResponseEntity.noContent().build();
    }
}
