package com.SWP391_02.controller;
import com.SWP391_02.dto.HistoryEventDTO;
import com.SWP391_02.dto.WarrantyLookupResponse;
import com.SWP391_02.service.WarrantyService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.security.SecurityRequirement;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.web.bind.annotation.*;

import java.time.LocalDate;

@RestController
@RequestMapping("/api/warranty")
@RequiredArgsConstructor
@SecurityRequirement(name = "bearerAuth") // nếu bạn dùng JWT + Swagger
public class WarrantyController {

    private final WarrantyService warrantyService;

    @Operation(summary = "Tra cứu bảo hành theo VIN (asOf=yyyy-MM-dd, mặc định hôm nay)")
    @GetMapping("/lookup")
    public WarrantyLookupResponse lookup(
            @RequestParam String vin,
            @RequestParam(required = false) String asOf
    ) {
        LocalDate date = (asOf == null || asOf.isBlank()) ? LocalDate.now() : LocalDate.parse(asOf);
        return warrantyService.lookup(vin, date);
    }

    @Operation(summary = "Xem lịch sử bảo hành (events) theo VIN, phân trang")
    @GetMapping("/history")
    public Page<HistoryEventDTO> history(
            @RequestParam String vin,
            @RequestParam(defaultValue = "0") int page,
            @RequestParam(defaultValue = "20") int size
    ) {
        return warrantyService.history(vin, PageRequest.of(page, size));
    }
}