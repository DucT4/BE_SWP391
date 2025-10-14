package com.SWP391_02.service;

import com.SWP391_02.entity.Quotation;
import com.SWP391_02.repository.QuotationRepository;
import jakarta.transaction.Transactional;
import lombok.RequiredArgsConstructor;
import org.springframework.http.*;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;
import java.util.Optional;

@Service
@RequiredArgsConstructor
@Transactional
public class QuotationService {

    private final QuotationRepository quotationRepo;

    /**
     * Lấy danh sách tất cả báo giá
     */
    public ResponseEntity<?> getAll() {
        return ResponseEntity.ok(quotationRepo.findAll());
    }

    /**
     * Lấy báo giá theo ID
     */
    public ResponseEntity<?> getOne(Long id) {
        Optional<Quotation> q = quotationRepo.findById(id);
        if (q.isEmpty()) {
            return ResponseEntity.status(HttpStatus.NOT_FOUND).body("Không tìm thấy báo giá");
        }
        return ResponseEntity.ok(q.get());
    }

    /**
     * Cập nhật trạng thái báo giá (Manager/EVM duyệt)
     */
    public ResponseEntity<?> updateStatus(Long id, String newStatus, String note) {
        Optional<Quotation> optional = quotationRepo.findById(id);
        if (optional.isEmpty()) {
            return ResponseEntity.status(HttpStatus.NOT_FOUND).body("Báo giá không tồn tại");
        }

        Quotation q = optional.get();
        q.setStatus(newStatus.toUpperCase());
        if (note != null && !note.isBlank()) {
            q.setNote(note);
        }
        // Cập nhật thời gian nếu cần log
        q.setCreatedAt(LocalDateTime.now());
        quotationRepo.save(q);
        return ResponseEntity.ok(q);
    }

    /**
     * Xóa báo giá
     */
    public ResponseEntity<?> delete(Long id) {
        if (!quotationRepo.existsById(id)) {
            return ResponseEntity.notFound().build();
        }
        quotationRepo.deleteById(id);
        return ResponseEntity.noContent().build();
    }
}
