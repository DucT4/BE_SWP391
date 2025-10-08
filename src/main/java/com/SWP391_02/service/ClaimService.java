package com.SWP391_02.service;

import com.SWP391_02.dto.ClaimRequest;
import com.SWP391_02.dto.ClaimResponse;
import com.SWP391_02.entity.Claim;
import com.SWP391_02.entity.ServiceCenters;
import com.SWP391_02.entity.User;
import com.SWP391_02.entity.Vehicles;
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

        // Create new claim with OPEN status
        Claim claim = Claim.builder()
                .vin(request.getVin())
                .openedBy(userId)
                .serviceCenterId(request.getServiceCenterId())
                .status(ClaimStatus.OPEN)
                .failureDesc(request.getFailureDesc())
                .createdAt(LocalDateTime.now())
                .build();

        // Save claim
        Claim savedClaim = claimRepo.save(claim);

        // Return response DTO
        return new ClaimResponse(
                savedClaim.getId(),
                savedClaim.getVin(),
                savedClaim.getOpenedBy(),
                user.getUsername(), // assuming User has username field
                savedClaim.getServiceCenterId(),
                serviceCenter.getName(),
                savedClaim.getStatus(),
                savedClaim.getFailureDesc(),
                savedClaim.getApprovalLevel(),
                savedClaim.getCreatedAt(),
                savedClaim.getUpdatedAt()
        );
    }
}

