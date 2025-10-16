package com.SWP391_02.controller;

import com.SWP391_02.entity.RepairItem;
import com.SWP391_02.service.RepairItemService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/repair-items")
@RequiredArgsConstructor
public class RepairItemController {

    private final RepairItemService repairService;

    @PostMapping("/confirm/{claimId}/{techId}")
    public ResponseEntity<String> confirmRepairItems(
            @PathVariable Long claimId,
            @PathVariable Long techId,
            @RequestBody List<RepairItem> items) {

        String result = repairService.confirmRepairItems(claimId, techId, items);
        return ResponseEntity.ok(result);
    }
}
