// src/main/java/com/SWP391_02/controller/CampaignController.java
package com.SWP391_02.controller;

import com.SWP391_02.dto.AddVinRequest;
import com.SWP391_02.dto.CampaignRecordRequest;
import com.SWP391_02.dto.CreateCampaignRequest;
import com.SWP391_02.entity.CampaignVin;
import com.SWP391_02.service.CampaignService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.security.SecurityRequirement;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/campaigns")
@RequiredArgsConstructor
@SecurityRequirement(name = "bearerAuth")
public class CampaignController {

    private final CampaignService campaignService;

    @Operation(summary = "Admin tạo campaign mới (EVM_ADMIN)")
    @PreAuthorize("hasAuthority('EVM_ADMIN')")
    @PostMapping
    public ResponseEntity<?> createCampaign(@Valid @RequestBody CreateCampaignRequest req,
                                            @RequestHeader("X-User-Id") Long userId) {
        return campaignService.createCampaign(req, userId);
    }

    @Operation(summary = "Admin thêm VIN vào campaign (EVM_ADMIN, EVM_STAFF)")
    @PreAuthorize("hasAnyAuthority('EVM_ADMIN', 'EVM_STAFF')")
    @PostMapping("/{campaignId}/vins")
    public ResponseEntity<?> addVins(@PathVariable Long campaignId,
                                     @Valid @RequestBody AddVinRequest req) {
        return campaignService.addVins(campaignId, req);
    }

    @Operation(summary = "Xem danh sách VIN trong campaign (EVM + SC)")
    @PreAuthorize("hasAnyAuthority('EVM_ADMIN', 'EVM_STAFF', 'SC_MANAGER', 'SC_TECHNICIAN')")
    @GetMapping("/{campaignId}/vins")
    public List<CampaignVin> listVins(@PathVariable Long campaignId) {
        return campaignService.listVins(campaignId);
    }

    @Operation(summary = "Cập nhật campaign (EVM_ADMIN)")
    @PreAuthorize("hasAuthority('EVM_ADMIN')")
    @PutMapping("/{id}")
    public ResponseEntity<?> updateCampaign(@PathVariable Long id,
                                            @Valid @RequestBody CreateCampaignRequest req) {
        return campaignService.updateCampaign(id, req);
    }

    @Operation(summary = "Xóa campaign (EVM_ADMIN)")

    @DeleteMapping("/{id}")
    public ResponseEntity<?> deleteCampaign(@PathVariable Long id) {
        return campaignService.deleteCampaign(id);
    }
    @Operation(summary = "SC_STAFF xác nhận nhận chiến dịch")
    @PostMapping("/{campaignId}/receive")
    public ResponseEntity<?> receiveCampaign(@PathVariable Long campaignId,
                                             @RequestHeader("X-User-Id") Long userId) {
        return campaignService.receiveCampaignOrder(campaignId, userId);
    }

    @Operation(summary = "SC_STAFF tạo đơn chiến dịch")
    @PostMapping("/{campaignId}/records")
    public ResponseEntity<?> createCampaignRecord(@PathVariable Long campaignId,
                                                  @Valid @RequestBody CampaignRecordRequest req,
                                                  @RequestHeader("X-User-Id") Long userId) {
        return campaignService.createCampaignRecord(campaignId, req, userId);
    }

}
