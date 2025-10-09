package com.SWP391_02.controller;

import com.SWP391_02.dto.ClaimRequest;
import com.SWP391_02.dto.ClaimResponse;
import com.SWP391_02.dto.MessageResponse;
import com.SWP391_02.service.ClaimService;
import com.SWP391_02.service.TokenService;
import jakarta.persistence.EntityNotFoundException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api")
@RequiredArgsConstructor
public class ClaimController {

    private final ClaimService claimService;
    private final TokenService tokenService;

    @PostMapping ("/claims")
    public ResponseEntity<?> createClaim(@Valid @RequestBody ClaimRequest request,
                                        HttpServletRequest httpRequest) {
        try {
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

            return ResponseEntity.status(HttpStatus.CREATED).body(response);

        } catch (EntityNotFoundException e) {
            return ResponseEntity.badRequest()
                    .body(new MessageResponse("Lỗi: " + e.getMessage()));
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                    .body(new MessageResponse("Lỗi hệ thống: " + e.getMessage()));
        }
    }
}
