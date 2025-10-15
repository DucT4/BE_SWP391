package com.SWP391_02.controller;

import com.SWP391_02.dto.ClaimAssignmentResponse;
import com.SWP391_02.dto.MessageResponse;
import com.SWP391_02.entity.ClaimAssignment;
import com.SWP391_02.service.ClaimAssignmentService;
import com.SWP391_02.service.TokenService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.security.SecurityRequirement;
import jakarta.persistence.EntityNotFoundException;
import jakarta.servlet.http.HttpServletRequest;
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
     * SC_MANAGER phân công claim cho technician
     */
    @Operation(summary = "Phân công claim cho kỹ thuật viên (SC_MANAGER)")
    @PreAuthorize("hasAuthority('ROLE_SC_MANAGER')")
    @PostMapping("/assign")
    public ResponseEntity<?> assignClaim(@RequestParam Long claimId,
                                         @RequestParam Long technicianId,
                                         @RequestParam(required = false) String note,
                                         HttpServletRequest httpRequest) {
        try {
            Long managerId = tokenService.getUserIdFromRequest(httpRequest);
            if (managerId == null) {
                return ResponseEntity.status(HttpStatus.UNAUTHORIZED)
                        .body(new MessageResponse("Token không hợp lệ"));
            }

            ClaimAssignment assignment = assignmentService.assignClaimToTechnician(
                    claimId, technicianId, managerId, note);

            // Convert entity sang DTO thông qua service
            ClaimAssignmentResponse response = assignmentService.toResponse(assignment);

            log.info("Phân công claim {} cho technician {} thành công", claimId, technicianId);
            return ResponseEntity.ok(response);

        } catch (EntityNotFoundException | IllegalStateException e) {
            log.error("Lỗi khi phân công claim: {}", e.getMessage());
            return ResponseEntity.badRequest().body(new MessageResponse("Lỗi: " + e.getMessage()));
        } catch (Exception e) {
            log.error("Lỗi hệ thống khi phân công claim", e);
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                    .body(new MessageResponse("Lỗi hệ thống: " + e.getMessage()));
        }
    }

    /**
     * SC_TECHNICIAN chấp nhận assignment
     */
    @Operation(summary = "Chấp nhận assignment (SC_TECHNICIAN)")
    @PreAuthorize("hasAuthority('ROLE_SC_TECHNICIAN')")
    @PutMapping("/{assignmentId}/accept")
    public ResponseEntity<?> acceptAssignment(@PathVariable Long assignmentId,
                                              HttpServletRequest httpRequest) {
        try {
            Long technicianId = tokenService.getUserIdFromRequest(httpRequest);
            if (technicianId == null) {
                return ResponseEntity.status(HttpStatus.UNAUTHORIZED)
                        .body(new MessageResponse("Token không hợp lệ"));
            }

            assignmentService.acceptAssignment(assignmentId, technicianId);
            log.info("Technician {} đã chấp nhận assignment {}", technicianId, assignmentId);
            return ResponseEntity.ok(new MessageResponse("Đã chấp nhận công việc"));

        } catch (EntityNotFoundException | IllegalStateException e) {
            log.error("Lỗi khi chấp nhận assignment: {}", e.getMessage());
            return ResponseEntity.badRequest().body(new MessageResponse("Lỗi: " + e.getMessage()));
        } catch (Exception e) {
            log.error("Lỗi hệ thống khi chấp nhận assignment", e);
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                    .body(new MessageResponse("Lỗi hệ thống: " + e.getMessage()));
        }
    }

    /**
     * SC_TECHNICIAN bắt đầu làm việc
     */
    @Operation(summary = "Bắt đầu làm việc (SC_TECHNICIAN)")
    @PreAuthorize("hasAuthority('ROLE_SC_TECHNICIAN')")
    @PutMapping("/{assignmentId}/start")
    public ResponseEntity<?> startWork(@PathVariable Long assignmentId,
                                       HttpServletRequest httpRequest) {
        try {
            Long technicianId = tokenService.getUserIdFromRequest(httpRequest);
            if (technicianId == null) {
                return ResponseEntity.status(HttpStatus.UNAUTHORIZED)
                        .body(new MessageResponse("Token không hợp lệ"));
            }

            assignmentService.startWork(assignmentId, technicianId);
            log.info("Technician {} đã bắt đầu làm việc assignment {}", technicianId, assignmentId);
            return ResponseEntity.ok(new MessageResponse("Đã bắt đầu làm việc"));

        } catch (EntityNotFoundException | IllegalStateException e) {
            log.error("Lỗi khi bắt đầu làm việc: {}", e.getMessage());
            return ResponseEntity.badRequest().body(new MessageResponse("Lỗi: " + e.getMessage()));
        } catch (Exception e) {
            log.error("Lỗi hệ thống khi bắt đầu làm việc", e);
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                    .body(new MessageResponse("Lỗi hệ thống: " + e.getMessage()));
        }
    }

    /**
     * SC_TECHNICIAN hoàn thành công việc
     */
    @Operation(summary = "Hoàn thành công việc (SC_TECHNICIAN)")
    @PreAuthorize("hasAuthority('ROLE_SC_TECHNICIAN')")
    @PutMapping("/{assignmentId}/complete")
    public ResponseEntity<?> completeWork(@PathVariable Long assignmentId,
                                          HttpServletRequest httpRequest) {
        try {
            Long technicianId = tokenService.getUserIdFromRequest(httpRequest);
            if (technicianId == null) {
                return ResponseEntity.status(HttpStatus.UNAUTHORIZED)
                        .body(new MessageResponse("Token không hợp lệ"));
            }

            assignmentService.completeWork(assignmentId, technicianId);
            log.info("Technician {} đã hoàn thành assignment {}", technicianId, assignmentId);
            return ResponseEntity.ok(new MessageResponse("Đã hoàn thành công việc"));

        } catch (EntityNotFoundException | IllegalStateException e) {
            log.error("Lỗi khi hoàn thành công việc: {}", e.getMessage());
            return ResponseEntity.badRequest().body(new MessageResponse("Lỗi: " + e.getMessage()));
        } catch (Exception e) {
            log.error("Lỗi hệ thống khi hoàn thành công việc", e);
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                    .body(new MessageResponse("Lỗi hệ thống: " + e.getMessage()));
        }
    }

    /**
     * Lấy danh sách assignments của technician
     */
    @Operation(summary = "Lấy danh sách assignments của technician (SC_TECHNICIAN)")
    @PreAuthorize("hasAuthority('ROLE_SC_TECHNICIAN')")
    @GetMapping("/my-assignments")
    public ResponseEntity<?> getMyAssignments(HttpServletRequest httpRequest) {
        try {
            Long technicianId = tokenService.getUserIdFromRequest(httpRequest);
            if (technicianId == null) {
                return ResponseEntity.status(HttpStatus.UNAUTHORIZED)
                        .body(new MessageResponse("Token không hợp lệ"));
            }

            List<ClaimAssignment> assignments = assignmentService.getAssignmentsByTechnician(technicianId);
            List<ClaimAssignmentResponse> response = assignmentService.toResponseList(assignments);
            return ResponseEntity.ok(response);

        } catch (Exception e) {
            log.error("Lỗi hệ thống khi lấy danh sách assignments", e);
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                    .body(new MessageResponse("Lỗi hệ thống: " + e.getMessage()));
        }
    }

    /**
     * Lấy danh sách assignments của một claim
     */
    @Operation(summary = "Lấy danh sách assignments của claim")
    @PreAuthorize("hasAnyAuthority('ROLE_SC_MANAGER', 'ROLE_SC_TECHNICIAN', 'ROLE_EVM_ADMIN')")
    @GetMapping("/by-claim/{claimId}")
    public ResponseEntity<?> getAssignmentsByClaim(@PathVariable Long claimId) {
        try {
            List<ClaimAssignment> assignments = assignmentService.getAssignmentsByClaim(claimId);
            List<ClaimAssignmentResponse> response = assignmentService.toResponseList(assignments);
            return ResponseEntity.ok(response);

        } catch (Exception e) {
            log.error("Lỗi hệ thống khi lấy danh sách assignments", e);
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                    .body(new MessageResponse("Lỗi hệ thống: " + e.getMessage()));
        }
    }
}
