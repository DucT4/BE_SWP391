package com.SWP391_02.service;

import com.SWP391_02.dto.ClaimAssignmentRequest;
import com.SWP391_02.dto.ClaimAssignmentResponse;
import com.SWP391_02.entity.Claim;
import com.SWP391_02.entity.ClaimAssignment;
import com.SWP391_02.entity.User;
import com.SWP391_02.enums.AssignmentStatus;
import com.SWP391_02.enums.ClaimStatus;
import com.SWP391_02.enums.Role;
import com.SWP391_02.repository.ClaimAssignmentRepository;
import com.SWP391_02.repository.ClaimRepository;
import com.SWP391_02.repository.UserRepository;
import jakarta.persistence.EntityNotFoundException;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;
import java.util.List;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class ClaimAssignmentService {

    private final ClaimAssignmentRepository assignmentRepository;
    private final ClaimRepository claimRepository;
    private final UserRepository userRepository;
    private final ClaimStatusHistoryService historyService;

    @Transactional
    public ClaimAssignmentResponse assignClaimToTechnician(ClaimAssignmentRequest request, Long managerId) {
        // Validate claim exists
        Claim claim = claimRepository.findById(request.getClaimId())
                .orElseThrow(() -> new EntityNotFoundException("Claim với ID " + request.getClaimId() + " không tồn tại"));

        // Validate claim status - only approved claims can be assigned
        if (claim.getStatus() != ClaimStatus.APPROVED) {
            throw new IllegalStateException("Chỉ có thể assign claim đã được APPROVED");
        }

        // Validate technician exists and has correct role
        User technician = userRepository.findById(request.getTechnicianId())
                .orElseThrow(() -> new EntityNotFoundException("Technician với ID " + request.getTechnicianId() + " không tồn tại"));

        if (technician.getRole() != Role.SC_TECHNICIAN) {
            throw new IllegalArgumentException("User phải có role SC_TECHNICIAN");
        }

        // Validate manager exists
        User manager = userRepository.findById(managerId)
                .orElseThrow(() -> new EntityNotFoundException("Manager với ID " + managerId + " không tồn tại"));

        // Check if claim already has an active assignment
        assignmentRepository.findByClaimIdAndStatus(request.getClaimId(), AssignmentStatus.ASSIGNED.name())
                .ifPresent(existing -> {
                    throw new IllegalStateException("Claim đã được assign cho technician khác");
                });

        // Create assignment
        ClaimAssignment assignment = ClaimAssignment.builder()
                .claimId(request.getClaimId())
                .technicianId(request.getTechnicianId())
                .assignedBy(managerId)
                .status(AssignmentStatus.ASSIGNED)
                .assignedAt(LocalDateTime.now())
                .note(request.getNote())
                .build();

        ClaimAssignment savedAssignment = assignmentRepository.save(assignment);

        // Update claim status to ASSIGNED
        claim.setStatus(ClaimStatus.ASSIGNED);
        claim.setUpdatedAt(LocalDateTime.now());
        claimRepository.save(claim);

        // Log history
        historyService.log(claim, ClaimStatus.ASSIGNED.name(), manager,
                "Claim được assign cho technician: " + technician.getUsername());

        return mapToResponse(savedAssignment, technician, manager);
    }


    public List<ClaimAssignmentResponse> getAssignmentsByTechnician(Long technicianId) {
        List<ClaimAssignment> assignments = assignmentRepository.findByTechnicianId(technicianId);
        return assignments.stream()
                .map(a -> {
                    User technician = userRepository.findById(a.getTechnicianId()).orElse(null);
                    User assignedBy = userRepository.findById(a.getAssignedBy()).orElse(null);
                    return mapToResponse(a, technician, assignedBy);
                })
                .collect(Collectors.toList());
    }

    public List<ClaimAssignmentResponse> getAssignmentsByClaim(Long claimId) {
        List<ClaimAssignment> assignments = assignmentRepository.findByClaimId(claimId);
        return assignments.stream()
                .map(a -> {
                    User technician = userRepository.findById(a.getTechnicianId()).orElse(null);
                    User assignedBy = userRepository.findById(a.getAssignedBy()).orElse(null);
                    return mapToResponse(a, technician, assignedBy);
                })
                .collect(Collectors.toList());
    }

    private ClaimAssignmentResponse mapToResponse(ClaimAssignment assignment, User technician, User assignedBy) {
        return ClaimAssignmentResponse.builder()
                .id(assignment.getId())
                .claimId(assignment.getClaimId())
                .technicianId(assignment.getTechnicianId())
                .technicianName(technician != null ? technician.getUsername() : null)
                .assignedBy(assignment.getAssignedBy())
                .assignedByName(assignedBy != null ? assignedBy.getUsername() : null)
                .status(assignment.getStatus() != null ? assignment.getStatus().name() : null)
                .assignedAt(assignment.getAssignedAt())
                .acceptedAt(assignment.getAcceptedAt())
                .startedAt(assignment.getStartedAt())
                .completedAt(assignment.getCompletedAt())
                .note(assignment.getNote())
                .build();
    }
}
