package com.SWP391_02.controller;

import com.SWP391_02.entity.Shipment;
import com.SWP391_02.service.ShipmentService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.security.SecurityRequirement;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import java.util.List;

@SecurityRequirement(name = "bearerAuth")
@RestController
@RequestMapping("/api/shipments")
@RequiredArgsConstructor
public class ShipmentController {

    private final ShipmentService service;

    @Operation(summary = "Tạo vận đơn", description = "EVM Staff tạo vận đơn giao phụ tùng cho Service Center.")
    @PostMapping
    public ResponseEntity<Shipment> createShipment(
            @RequestParam Long allocationId,
            @RequestParam List<Long> partIds,
            @RequestParam List<Integer> qtys) {
        return ResponseEntity.ok(service.createShipment(allocationId, partIds, qtys));
    }

    @Operation(summary = "Danh sách vận đơn", description = "Hiển thị tất cả vận đơn hiện tại (đang giao hoặc đã giao).")
    @GetMapping
    public ResponseEntity<List<Shipment>> getAll() {
        return ResponseEntity.ok(service.getAllShipments());
    }

    @Operation(summary = "Xác nhận giao hàng", description = "Service Center xác nhận đã nhận đủ phụ tùng từ vận đơn.")
    @PutMapping("/{id}/delivered")
    public ResponseEntity<Shipment> markDelivered(@PathVariable Long id) {
        return ResponseEntity.ok(service.markDelivered(id));
    }
}
