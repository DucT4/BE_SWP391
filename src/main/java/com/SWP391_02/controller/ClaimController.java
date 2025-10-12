package com.SWP391_02.controller;

import com.SWP391_02.dto.ClaimApprovalDTO;
import com.SWP391_02.dto.ClaimRequest;
import com.SWP391_02.dto.ClaimResponse;
import com.SWP391_02.dto.MessageResponse;
import com.SWP391_02.entity.User;
import com.SWP391_02.repository.UserRepository;
import com.SWP391_02.service.ClaimService;
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
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/claims")
@RequiredArgsConstructor
@SecurityRequirement(name = "bearerAuth")
@Slf4j
public class ClaimController {

    private final ClaimService claimService;
    private final TokenService tokenService;
    private final UserRepository userRepository;

    /**
     * SC_TECHNICIAN tạo claim mới
     */
    @Operation(summary = "Tạo claim mới (SC_TECHNICIAN)")
    @PostMapping
    public ResponseEntity<?> createClaim(@Valid @RequestBody ClaimRequest request,
                                        HttpServletRequest httpRequest) {
        try {
            log.info("Nhận request tạo claim: vin={}, serviceCenterId={}", request.getVin(), request.getServiceCenterId());

            // Extract user ID from JWT token
            Long userId = tokenService.getUserIdFromRequest(httpRequest);

            if (userId == null) {
                return ResponseEntity.status(HttpStatus.UNAUTHORIZED)
                        .body(new MessageResponse("Token không hợp lệ hoặc không tồn tại"));
            }

            // Extract role from JWT token
            String userRole = tokenService.getRoleFromRequest(httpRequest);

            // Kiểm tra quyền SC_TECHNICIAN
            if (userRole == null || !userRole.equals("ROLE_SC_TECHNICIAN")) {
                return ResponseEntity.status(HttpStatus.FORBIDDEN)
                        .body(new MessageResponse("Chỉ có SC Technician mới có quyền tạo claim"));
            }

            // Create claim
            ClaimResponse response = claimService.createClaim(request, userId);
            log.info("Tạo claim thành công: id={}", response.getId());

            return ResponseEntity.status(HttpStatus.CREATED).body(response);

        } catch (EntityNotFoundException e) {
            log.error("Lỗi EntityNotFound: {}", e.getMessage());
            return ResponseEntity.badRequest()
                    .body(new MessageResponse("Lỗi: " + e.getMessage()));
        } catch (Exception e) {
            log.error("Lỗi hệ thống khi tạo claim", e);
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                    .body(new MessageResponse("Lỗi hệ thống: " + e.getMessage()));
        }
    }

    /**
     * SC_MANAGER gửi claim lên hãng
     */
    @Operation(summary = "Gửi claim lên hãng (SC_MANAGER)")
    @PutMapping("/{id}/submit")
    public ResponseEntity<?> submitClaim(@PathVariable Long id,
                                        @RequestParam(required = false) String remark,
                                        HttpServletRequest httpRequest) {
        try {
            log.info("Nhận request submit claim: claimId={}, remark={}", id, remark);

            Long userId = tokenService.getUserIdFromRequest(httpRequest);

            if (userId == null) {
                return ResponseEntity.status(HttpStatus.UNAUTHORIZED)
                        .body(new MessageResponse("Token không hợp lệ"));
            }

            String userRole = tokenService.getRoleFromRequest(httpRequest);
            log.info("User role: {}", userRole);

            if (!"ROLE_SC_MANAGER".equals(userRole)) {
                return ResponseEntity.status(HttpStatus.FORBIDDEN)
                        .body(new MessageResponse("Chỉ có SC Manager mới có quyền gửi claim lên hãng"));
            }

            User manager = userRepository.findById(userId)
                    .orElseThrow(() -> new EntityNotFoundException("User không tồn tại"));

            claimService.submitToEvm(id, manager, remark != null ? remark : "");
            log.info("Submit claim thành công: claimId={}", id);

            return ResponseEntity.ok(new MessageResponse("Đã gửi claim lên hãng!"));

        } catch (EntityNotFoundException e) {
            log.error("Lỗi EntityNotFound: {}", e.getMessage());
            return ResponseEntity.badRequest()
                    .body(new MessageResponse("Lỗi: " + e.getMessage()));
        } catch (IllegalStateException e) {
            log.error("Lỗi trạng thái: {}", e.getMessage());
            return ResponseEntity.badRequest()
                    .body(new MessageResponse("Lỗi: " + e.getMessage()));
        } catch (Exception e) {
            log.error("Lỗi hệ thống khi submit claim", e);
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                    .body(new MessageResponse("Lỗi hệ thống: " + e.getMessage()));
        }
    }

    /**
     * EVM_STAFF duyệt chính thức
     */
    @Operation(summary = "Duyệt claim (EVM_STAFF)")
    @PutMapping("/approve")
    public ResponseEntity<?> approveClaim(@Valid @RequestBody ClaimApprovalDTO dto,
                                         HttpServletRequest httpRequest) {
        try {
            log.info("Nhận request approve claim: claimId={}, decision={}", dto.getClaimId(), dto.getDecision());

            Long userId = tokenService.getUserIdFromRequest(httpRequest);

            if (userId == null) {
                return ResponseEntity.status(HttpStatus.UNAUTHORIZED)
                        .body(new MessageResponse("Token không hợp lệ"));
            }

            String userRole = tokenService.getRoleFromRequest(httpRequest);

            if (!"ROLE_EVM_STAFF".equals(userRole)) {
                return ResponseEntity.status(HttpStatus.FORBIDDEN)
                        .body(new MessageResponse("Chỉ có EVM Staff mới có quyền duyệt claim"));
            }

            User evmUser = userRepository.findById(userId)
                    .orElseThrow(() -> new EntityNotFoundException("User không tồn tại"));

            claimService.approveByEvm(dto, evmUser);
            log.info("Approve claim thành công: claimId={}, decision={}", dto.getClaimId(), dto.getDecision());

            return ResponseEntity.ok(new MessageResponse("Hãng đã cập nhật quyết định!"));

        } catch (EntityNotFoundException e) {
            log.error("Lỗi EntityNotFound: {}", e.getMessage());
            return ResponseEntity.badRequest()
                    .body(new MessageResponse("Lỗi: " + e.getMessage()));
        } catch (IllegalStateException e) {
            log.error("Lỗi trạng thái: {}", e.getMessage());
            return ResponseEntity.badRequest()
                    .body(new MessageResponse("Lỗi: " + e.getMessage()));
        } catch (Exception e) {
            log.error("Lỗi hệ thống khi approve claim", e);
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                    .body(new MessageResponse("Lỗi hệ thống: " + e.getMessage()));
        }
    }
}
