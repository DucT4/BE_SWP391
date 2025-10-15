package com.SWP391_02.controller;

import com.SWP391_02.dto.ClaimAssignmentRequest;
import com.SWP391_02.dto.ClaimAssignmentResponse;
import com.SWP391_02.dto.MessageResponse;
import com.SWP391_02.service.ClaimAssignmentService;
import com.SWP391_02.service.TokenService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.security.SecurityRequirement;
import jakarta.persistence.EntityNotFoundException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/claim-assignments")
@RequiredArgsConstructor
@SecurityRequirement(name = "bearerAuth")
@Slf4j
public class ClaimAssignmentController {

    private final ClaimAssignmentService assignmentService;
    private final TokenService tokenService;

    /**
     * Manager assign claim cho technician
     */
    @Operation(summary = "Manager assign claim cho technician (SC_MANAGER)")
    @PreAuthorize("hasAuthority('SC_MANAGER')")
    @PostMapping("/assign")
    public ResponseEntity<?> assignClaim(@Valid @RequestBody ClaimAssignmentRequest request,
                                        HttpServletRequest httpRequest) {
        try {
            log.info("Nhận request assign claim: claimId={}, technicianId={}",
                    request.getClaimId(), request.getTechnicianId());

            Long managerId = tokenService.getUserIdFromRequest(httpRequest);
            if (managerId == null) {
                return ResponseEntity.status(HttpStatus.UNAUTHORIZED)
                        .body(new MessageResponse("Token không hợp lệ"));
            }

            ClaimAssignmentResponse response = assignmentService.assignClaimToTechnician(request, managerId);

            return ResponseEntity.ok(response);

        } catch (EntityNotFoundException e) {
            log.error("Entity not found: {}", e.getMessage());
            return ResponseEntity.status(HttpStatus.NOT_FOUND)
                    .body(new MessageResponse(e.getMessage()));
        } catch (IllegalStateException | IllegalArgumentException e) {
            log.error("Business logic error: {}", e.getMessage());
            return ResponseEntity.status(HttpStatus.BAD_REQUEST)
                    .body(new MessageResponse(e.getMessage()));
        } catch (Exception e) {
            log.error("Lỗi khi assign claim", e);
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                    .body(new MessageResponse("Lỗi hệ thống: " + e.getMessage()));
        }
    }

    /**
     * Lấy danh sách assignment của technician
     */
    @Operation(summary = "Lấy danh sách assignment của technician (SC_TECHNICIAN)")
    @PreAuthorize("hasAuthority('SC_TECHNICIAN')")
    @GetMapping("/my-assignments")
    public ResponseEntity<?> getMyAssignments(HttpServletRequest httpRequest) {
        try {
            Long technicianId = tokenService.getUserIdFromRequest(httpRequest);
            if (technicianId == null) {
                return ResponseEntity.status(HttpStatus.UNAUTHORIZED)
                        .body(new MessageResponse("Token không hợp lệ"));
            }

            List<ClaimAssignmentResponse> assignments = assignmentService.getAssignmentsByTechnician(technicianId);

            return ResponseEntity.ok(assignments);

        } catch (Exception e) {
            log.error("Lỗi khi lấy assignments", e);
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                    .body(new MessageResponse("Lỗi hệ thống: " + e.getMessage()));
        }
    }

    /**
     * Lấy danh sách assignment của một claim
     */
    @Operation(summary = "Lấy danh sách assignment của claim (SC_MANAGER, EVM)")
    @PreAuthorize("hasAnyAuthority('SC_MANAGER', 'EVM')")
    @GetMapping("/by-claim/{claimId}")
    public ResponseEntity<?> getAssignmentsByClaim(@PathVariable Long claimId) {
        try {
            List<ClaimAssignmentResponse> assignments = assignmentService.getAssignmentsByClaim(claimId);

            return ResponseEntity.ok(assignments);

        } catch (Exception e) {
            log.error("Lỗi khi lấy assignments by claim", e);
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                    .body(new MessageResponse("Lỗi hệ thống: " + e.getMessage()));
        }
    }
}
