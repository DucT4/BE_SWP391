package com.SWP391_02.service;

import com.SWP391_02.repository.CampaignPaymentRepository;
import com.SWP391_02.repository.CampaignRecordRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import com.SWP391_02.entity.CampaignPayment;
import com.SWP391_02.dto.CampaignPaymentDTO;
import java.util.List;
import java.util.stream.Collectors;
import org.springframework.transaction.annotation.Transactional;
import java.util.Optional;
import com.SWP391_02.dto.ReportDTO;
import com.SWP391_02.entity.CampaignRecord;
import com.SWP391_02.entity.Customer;
import com.SWP391_02.entity.User;
import com.SWP391_02.enums.PaymentMethod;
import com.SWP391_02.enums.PaymentStatus;
import java.math.BigDecimal;

@Service
public class CampaignPaymentService {

    @Autowired
    private CampaignPaymentRepository campaignPaymentRepository;

    @Autowired
    private CampaignRecordRepository campaignRecordRepository;

    public CampaignPaymentDTO createPayment(CampaignPaymentDTO dto) {
        CampaignPayment payment = new CampaignPayment();
        payment.setAmount(dto.getAmount().doubleValue());
        payment.setPaymentDate(dto.getPaymentDate());
        payment.setMethod(PaymentMethod.valueOf(dto.getPaymentMethod()));
        payment.setStatus(PaymentStatus.valueOf(dto.getStatus()));
        // Liên kết campaignRecord từ campaignId
        if (dto.getCampaignId() != null) {
            campaignRecordRepository.findById(dto.getCampaignId()).ifPresent(payment::setCampaignRecord);
        }
        payment = campaignPaymentRepository.save(payment);
        return toDTO(payment);
    }

    public List<CampaignPaymentDTO> getAllPayments() {
        return campaignPaymentRepository.findAll().stream().map(this::toDTO).collect(Collectors.toList());
    }

    public CampaignPaymentDTO getPaymentById(Long id) {
        Optional<com.SWP391_02.entity.CampaignPayment> payment = campaignPaymentRepository.findById(id);
        return payment.map(this::toDTO).orElse(null);
    }

    @Transactional
    public CampaignPaymentDTO updatePayment(Long id, CampaignPaymentDTO dto) {
        Optional<com.SWP391_02.entity.CampaignPayment> opt = campaignPaymentRepository.findById(id);
        if (opt.isPresent()) {
            CampaignPayment payment = opt.get();
            payment.setAmount(dto.getAmount().doubleValue());
            payment.setPaymentDate(dto.getPaymentDate());
            payment.setMethod(PaymentMethod.valueOf(dto.getPaymentMethod()));
            payment.setStatus(PaymentStatus.valueOf(dto.getStatus()));
            // TODO: update other fields
            campaignPaymentRepository.save(payment);
            return toDTO(payment);
        }
        return null;
    }

    public void deletePayment(Long id) {
        campaignPaymentRepository.deleteById(id);
    }

    public List<CampaignPaymentDTO> getPaymentsByCampaign(Long campaignId) {
        List<CampaignPayment> payments = campaignPaymentRepository.findAll().stream()
            .filter(p -> p.getCampaignRecord() != null && p.getCampaignRecord().getId().equals(campaignId))
            .collect(Collectors.toList());
        return payments.stream().map(this::toDTO).collect(Collectors.toList());
    }

    public ReportDTO getCampaignPaymentReport(Long campaignId) {
        List<CampaignPayment> payments = campaignPaymentRepository.findAll().stream()
            .filter(p -> p.getCampaignRecord() != null && p.getCampaignRecord().getId().equals(campaignId))
            .collect(Collectors.toList());
        BigDecimal totalAmount = payments.stream()
            .map(p -> BigDecimal.valueOf(p.getAmount()))
            .reduce(BigDecimal.ZERO, BigDecimal::add);
        int paymentCount = payments.size();
        String statusSummary = payments.stream()
            .collect(Collectors.groupingBy(p -> p.getStatus().name(), Collectors.counting()))
            .toString();
        ReportDTO report = new ReportDTO();
        report.setCampaignId(campaignId);
        report.setTotalAmount(totalAmount);
        report.setPaymentCount(paymentCount);
        report.setStatusSummary(statusSummary);
        return report;
    }

    private CampaignPaymentDTO toDTO(CampaignPayment payment) {
        CampaignPaymentDTO dto = new CampaignPaymentDTO();
        dto.setId(payment.getId());
        dto.setAmount(java.math.BigDecimal.valueOf(payment.getAmount()));
        dto.setPaymentDate(payment.getPaymentDate());
        dto.setPaymentMethod(payment.getMethod().name());
        dto.setStatus(payment.getStatus().name());
        if (payment.getCampaignRecord() != null) {
            dto.setCampaignId(payment.getCampaignRecord().getId());
        }
        return dto;
    }

}
