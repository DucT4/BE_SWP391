package com.SWP391_02.controller;

import com.SWP391_02.service.CampaignPaymentService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;
import com.SWP391_02.dto.CampaignPaymentDTO;
import java.util.List;

@RestController
@RequestMapping("/api/campaign-payments")
public class CampaignPaymentController {
    @Autowired
    private CampaignPaymentService campaignPaymentService;

    @PostMapping
    public CampaignPaymentDTO createPayment(@RequestBody CampaignPaymentDTO dto) {
        return campaignPaymentService.createPayment(dto);
    }

    @GetMapping
    public List<CampaignPaymentDTO> getAllPayments() {
        return campaignPaymentService.getAllPayments();
    }

    @GetMapping("/{id}")
    public CampaignPaymentDTO getPaymentById(@PathVariable Long id) {
        return campaignPaymentService.getPaymentById(id);
    }

    @PutMapping("/{id}")
    public CampaignPaymentDTO updatePayment(@PathVariable Long id, @RequestBody CampaignPaymentDTO dto) {
        return campaignPaymentService.updatePayment(id, dto);
    }

    @DeleteMapping("/{id}")
    public void deletePayment(@PathVariable Long id) {
        campaignPaymentService.deletePayment(id);
    }

    @GetMapping("/report")
    public Object getCampaignPaymentReport(@RequestParam Long campaignId) {
        return campaignPaymentService.getCampaignPaymentReport(campaignId);
    }
}
