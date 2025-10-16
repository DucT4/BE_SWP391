package com.SWP391_02.service;

import com.SWP391_02.dto.ClaimAssignmentResponse;
import com.SWP391_02.entity.Claim;
import com.SWP391_02.entity.ClaimAssignment;
import com.SWP391_02.entity.User;
import com.SWP391_02.enums.ClaimAssignmentStatus;
import com.SWP391_02.enums.ClaimStatus;
import com.SWP391_02.repository.ClaimAssignmentRepository;
import com.SWP391_02.repository.ClaimRepository;
import com.SWP391_02.repository.UserRepository;
import jakarta.persistence.EntityNotFoundException;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;
import java.util.List;

@Service
@RequiredArgsConstructor
public class ClaimAssignmentService {

    private final ClaimAssignmentRepository assignmentRepo;
    private final ClaimRepository claimRepo;
    private final UserRepository userRepo;
    private final ClaimStatusHistoryService historyService;

    /**
     * SC_MANAGER phân công claim cho technician
     */
    @Transactional
    public ClaimAssignment assignClaimToTechnician(Long claimId, Long technicianId, Long managerId, String note) {
        Claim claim = claimRepo.findById(claimId)
                .orElseThrow(() -> new EntityNotFoundException("Claim không tồn tại"));

        // Kiểm tra claim phải ở trạng thái APPROVED
        if (claim.getStatus() != ClaimStatus.APPROVED) {
            throw new IllegalStateException("Chỉ có thể phân công claim đã được duyệt (APPROVED)");
        }

        User technician = userRepo.findById(technicianId)
                .orElseThrow(() -> new EntityNotFoundException("Technician không tồn tại"));

        User manager = userRepo.findById(managerId)
                .orElseThrow(() -> new EntityNotFoundException("Manager không tồn tại"));

        // Tạo assignment
        ClaimAssignment assignment = ClaimAssignment.builder()
                .claim(claim)
                .technician(technician)
                .assignedBy(manager)
                .status(ClaimAssignmentStatus.ASSIGNED)
                .assignedAt(LocalDateTime.now())
                .note(note)
                .build();

        ClaimAssignment saved = assignmentRepo.save(assignment);

        // Cập nhật trạng thái claim
        claim.setStatus(ClaimStatus.ASSIGNED);
        claim.setUpdatedAt(LocalDateTime.now());
        claimRepo.save(claim);

        // Ghi log
        historyService.log(claim, ClaimStatus.ASSIGNED.name(), manager,
                "Đã phân công cho kỹ thuật viên: " + technician.getUsername());

        return saved;
    }

    /**
     * Technician chấp nhận assignment
     */
    @Transactional
    public void acceptAssignment(Long assignmentId, Long technicianId) {
        ClaimAssignment assignment = assignmentRepo.findById(assignmentId)
                .orElseThrow(() -> new EntityNotFoundException("Assignment không tồn tại"));

        if (!assignment.getTechnician().getId().equals(technicianId)) {
            throw new IllegalStateException("Bạn không có quyền chấp nhận assignment này");
        }

        if (assignment.getStatus() != ClaimAssignmentStatus.ASSIGNED) {
            throw new IllegalStateException("Chỉ có thể chấp nhận assignment ở trạng thái ASSIGNED");
        }

        assignment.setStatus(ClaimAssignmentStatus.ACCEPTED);
        assignment.setAcceptedAt(LocalDateTime.now());
        assignmentRepo.save(assignment);
    }

    /**
     * Technician bắt đầu làm việc
     */
    @Transactional
    public void startWork(Long assignmentId, Long technicianId) {
        ClaimAssignment assignment = assignmentRepo.findById(assignmentId)
                .orElseThrow(() -> new EntityNotFoundException("Assignment không tồn tại"));

        if (!assignment.getTechnician().getId().equals(technicianId)) {
            throw new IllegalStateException("Bạn không có quyền bắt đầu assignment này");
        }

        if (assignment.getStatus() != ClaimAssignmentStatus.ACCEPTED) {
            throw new IllegalStateException("Chỉ có thể bắt đầu assignment đã được chấp nhận");
        }

        assignment.setStatus(ClaimAssignmentStatus.IN_PROGRESS);
        assignment.setStartedAt(LocalDateTime.now());
        assignmentRepo.save(assignment);

        User technician = assignment.getTechnician();
        historyService.log(assignment.getClaim(), ClaimStatus.ASSIGNED.name(), technician,
                "Kỹ thuật viên bắt đầu sửa chữa");
    }

    /**
     * Technician hoàn thành công việc
     */
    @Transactional
    public void completeWork(Long assignmentId, Long technicianId) {
        ClaimAssignment assignment = assignmentRepo.findById(assignmentId)
                .orElseThrow(() -> new EntityNotFoundException("Assignment không tồn tại"));

        if (!assignment.getTechnician().getId().equals(technicianId)) {
            throw new IllegalStateException("Bạn không có quyền hoàn thành assignment này");
        }

        if (assignment.getStatus() != ClaimAssignmentStatus.IN_PROGRESS) {
            throw new IllegalStateException("Chỉ có thể hoàn thành assignment đang thực hiện");
        }

        assignment.setStatus(ClaimAssignmentStatus.COMPLETED);
        assignment.setCompletedAt(LocalDateTime.now());
        assignmentRepo.save(assignment);

        // Cập nhật trạng thái claim
        Claim claim = assignment.getClaim();
        claim.setStatus(ClaimStatus.DONE);
        claim.setUpdatedAt(LocalDateTime.now());
        claimRepo.save(claim);

        User technician = assignment.getTechnician();
        historyService.log(claim, ClaimStatus.DONE.name(), technician,
                "Kỹ thuật viên hoàn thành sửa chữa");
    }

    @Transactional(readOnly = true)
    public List<ClaimAssignment> getAssignmentsByTechnician(Long technicianId) {
        return assignmentRepo.findByTechnicianId(technicianId);
    }

    @Transactional(readOnly = true)
    public List<ClaimAssignment> getAssignmentsByClaim(Long claimId) {
        return assignmentRepo.findByClaimId(claimId);
    }

    /**
     * Convert ClaimAssignment entity sang DTO
     */
    public ClaimAssignmentResponse toResponse(ClaimAssignment assignment) {
        return new ClaimAssignmentResponse(
                assignment.getId(),
                assignment.getClaim().getId(),
                assignment.getClaim().getVin(),
                assignment.getClaim().getFailureDesc(),
                assignment.getTechnician().getId(),
                assignment.getTechnician().getUsername(),
                assignment.getAssignedBy().getId(),
                assignment.getAssignedBy().getUsername(),
                assignment.getStatus().name(),
                assignment.getAssignedAt(),
                assignment.getAcceptedAt(),
                assignment.getStartedAt(),
                assignment.getCompletedAt(),
                assignment.getNote()
        );
    }

    /**
     * Convert List<ClaimAssignment> sang List<ClaimAssignmentResponse>
     */
    public List<ClaimAssignmentResponse> toResponseList(List<ClaimAssignment> assignments) {
        return assignments.stream()
                .map(this::toResponse)
                .toList();
    }
}
