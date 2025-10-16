package com.SWP391_02.service;

import com.SWP391_02.dto.AddVinRequest;
import com.SWP391_02.dto.CampaignRecordRequest;
import com.SWP391_02.dto.CreateCampaignRequest;
import com.SWP391_02.entity.Campaign;
import com.SWP391_02.entity.CampaignRecord;
import com.SWP391_02.entity.CampaignVin;
import com.SWP391_02.repository.CampaignRecordRepository;
import com.SWP391_02.repository.CampaignRepository;
import com.SWP391_02.repository.CampaignVinRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;

import java.net.URI;
import java.util.List;
import java.util.Map;
import java.util.Optional;

@Service
@RequiredArgsConstructor
public class CampaignService {

    private final CampaignRecordRepository campaignRecordRepository;
    private final CampaignRepository campaignRepo;
    private final CampaignVinRepository campaignVinRepo;

    // ✅ Tạo mới campaign
    public ResponseEntity<?> createCampaign(CreateCampaignRequest req, Long userId) {
        Campaign c = Campaign.builder()
                .type(req.getType())
                .name(req.getName())
                .description(req.getDescription())
                .startDate(req.getStartDate())
                .endDate(req.getEndDate())
                .status(req.getStatus())
                .createdBy(userId)
                .build();
        campaignRepo.save(c);
        return ResponseEntity
                .created(URI.create("/api/campaigns/" + c.getId()))
                .body(c);
    }

    // ✅ Thêm VINs vào campaign
    public ResponseEntity<?> addVins(Long campaignId, AddVinRequest req) {
        List<CampaignVin> vins = req.getVins().stream()
                .map(v -> CampaignVin.builder()
                        .campaignId(campaignId)
                        .vin(v)
                        .status("Planned")
                        .build())
                .toList();
        campaignVinRepo.saveAll(vins);
        return ResponseEntity.ok(vins.size() + " VIN(s) added to campaign " + campaignId);
    }

    // ✅ Danh sách VIN trong campaign
    public List<CampaignVin> listVins(Long campaignId) {
        return campaignVinRepo.findByCampaignId(campaignId);
    }

    // ✅ Cập nhật campaign
    public ResponseEntity<?> updateCampaign(Long id, CreateCampaignRequest req) {
        return campaignRepo.findById(id)
                .map(c -> {
                    c.setType(req.getType());
                    c.setName(req.getName());
                    c.setDescription(req.getDescription());
                    c.setStartDate(req.getStartDate());
                    c.setEndDate(req.getEndDate());
                    c.setStatus(req.getStatus());
                    campaignRepo.save(c);
                    return ResponseEntity.ok("Campaign updated successfully");
                })
                .orElse(ResponseEntity.notFound().build());
    }

    // ✅ Xóa campaign
    public ResponseEntity<?> deleteCampaign(Long id) {
        if (!campaignRepo.existsById(id))
            return ResponseEntity.notFound().build();
        campaignRepo.deleteById(id);
        return ResponseEntity.noContent().build();
    }

    // ⚙️ 1️⃣ SC_STAFF xác nhận nhận chiến dịch
    public ResponseEntity<?> receiveCampaignOrder(Long campaignId, Long staffId) {
        Optional<Campaign> campaign = campaignRepo.findById(campaignId);
        if (campaign.isEmpty()) {
            return ResponseEntity.badRequest().body(Map.of("message", "Chiến dịch không tồn tại"));
        }
        return ResponseEntity.ok(Map.of(
                "message", "SC_STAFF đã nhận chiến dịch ID=" + campaignId,
                "campaignId", campaignId,
                "staffId", staffId
        ));
    }

    // ⚙️ 2️⃣ SC_STAFF tạo đơn chiến dịch
    public ResponseEntity<?> createCampaignRecord(Long campaignId, CampaignRecordRequest req, Long staffId) {
        Optional<Campaign> campaign = campaignRepo.findById(campaignId);
        if (campaign.isEmpty()) {
            return ResponseEntity.badRequest().body(Map.of("message", "Chiến dịch không tồn tại"));
        }

        CampaignRecord record = CampaignRecord.builder()
                .campaignId(campaignId)
                .vin(req.getVin())
                .staffId(staffId)
                .performedDate(req.getPerformedDate())
                .workDescription(req.getWorkDescription())
                .technicianName(req.getTechnicianName())
                .build();

        campaignRecordRepository.save(record);
        return ResponseEntity.ok(record);
    }
}
