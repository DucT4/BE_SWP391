package com.SWP391_02.service;

import com.SWP391_02.dto.ClaimApprovalDTO;
import com.SWP391_02.dto.ClaimRequest;
import com.SWP391_02.dto.ClaimResponse;
import com.SWP391_02.entity.Claim;
import com.SWP391_02.entity.ServiceCenters;
import com.SWP391_02.entity.User;
import com.SWP391_02.entity.Vehicles;
import com.SWP391_02.enums.ApprovalLevel;
import com.SWP391_02.enums.ClaimStatus;
import com.SWP391_02.repository.ClaimRepository;
import com.SWP391_02.repository.ServiceCentersRepository;
import com.SWP391_02.repository.UserRepository;
import com.SWP391_02.repository.VehicleRepository;
import jakarta.persistence.EntityNotFoundException;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;

@Service
@RequiredArgsConstructor
public class ClaimService {

    private final ClaimRepository claimRepo;
    private final VehicleRepository vehicleRepository;
    private final ServiceCentersRepository serviceCentersRepository;
    private final UserRepository userRepository;
    private final ClaimApprovalService approvalService;
    private final ClaimStatusHistoryService historyService;

    @Transactional
    public ClaimResponse createClaim(ClaimRequest request, Long userId) {
        // Validate VIN exists
        Vehicles vehicle = vehicleRepository.findByVin(request.getVin())
                .orElseThrow(() -> new EntityNotFoundException("Vehicle với VIN " + request.getVin() + " không tồn tại"));

        // Validate service center exists
        ServiceCenters serviceCenter = serviceCentersRepository.findById(request.getServiceCenterId())
                .orElseThrow(() -> new EntityNotFoundException("Service Center với ID " + request.getServiceCenterId() + " không tồn tại"));

        // Validate user exists
        User user = userRepository.findById(userId)
                .orElseThrow(() -> new EntityNotFoundException("User với ID " + userId + " không tồn tại"));

        // Create new claim with DRAFT status
        Claim claim = Claim.builder()
                .vin(request.getVin())
                .openedBy(userId)
                .serviceCenterId(request.getServiceCenterId())
                .status(ClaimStatus.DRAFT)
                .failureDesc(request.getFailureDesc())
                .createdAt(LocalDateTime.now())
                .build();

        // Save claim
        Claim savedClaim = claimRepo.save(claim);

        // Log status history
        historyService.log(savedClaim, ClaimStatus.DRAFT.name(), user, "Claim được tạo bởi SC Technician");

        // Return response DTO
        return new ClaimResponse(
                savedClaim.getId(),
                savedClaim.getVin(),
                savedClaim.getOpenedBy(),
                user.getUsername(),
                savedClaim.getServiceCenterId(),
                serviceCenter.getName(),
                savedClaim.getStatus(),
                savedClaim.getFailureDesc(),
                savedClaim.getApprovalLevel(),
                savedClaim.getResolutionType(),
                savedClaim.getResolutionNote(),
                savedClaim.getCreatedAt(),
                savedClaim.getUpdatedAt()
        );
    }

    @Transactional
    public void submitToEvm(Long claimId, User manager, String remark) {
        Claim claim = claimRepo.findById(claimId)
                .orElseThrow(() -> new EntityNotFoundException("Claim không tồn tại"));

        // Kiểm tra trạng thái hợp lệ
        if (claim.getStatus() != ClaimStatus.DRAFT) {
            throw new IllegalStateException("Chỉ có thể gửi claim ở trạng thái DRAFT");
        }

        claim.setStatus(ClaimStatus.SENT_TO_EVM);
        claim.setApprovalLevel(ApprovalLevel.EVM);
        claim.setUpdatedAt(LocalDateTime.now());
        claimRepo.save(claim);

        approvalService.record(claim, manager, "MANAGER", "FORWARDED", remark);
        historyService.log(claim, ClaimStatus.SENT_TO_EVM.name(), manager, "Gửi lên hãng");
    }

    @Transactional
    public void approveByEvm(ClaimApprovalDTO dto, User evmUser) {
        Claim claim = claimRepo.findById(dto.getClaimId())
                .orElseThrow(() -> new EntityNotFoundException("Claim không tồn tại"));

        // Kiểm tra trạng thái hợp lệ
        if (claim.getStatus() != ClaimStatus.SENT_TO_EVM) {
            throw new IllegalStateException("Chỉ có thể duyệt claim ở trạng thái SENT_TO_EVM");
        }

        ClaimStatus newStatus = dto.getDecision().equals("APPROVED")
                ? ClaimStatus.APPROVED
                : ClaimStatus.REJECTED;

        claim.setStatus(newStatus);
        claim.setApprovalLevel(ApprovalLevel.EVM);
        claim.setUpdatedAt(LocalDateTime.now());
        claimRepo.save(claim);

        approvalService.record(claim, evmUser, "EVM", dto.getDecision(), dto.getRemark());
        historyService.log(claim, newStatus.name(), evmUser, "EVM quyết định: " + dto.getDecision());
    }
}
