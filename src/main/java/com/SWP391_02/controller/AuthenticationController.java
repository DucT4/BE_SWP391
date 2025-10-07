package com.SWP391_02.controller;


import com.SWP391_02.dto.LoginRequest;
import com.SWP391_02.dto.LoginResponse;
import com.SWP391_02.dto.RegisterRequest;
import com.SWP391_02.dto.UserResponse;
import com.SWP391_02.service.AuthenticationService;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RequiredArgsConstructor
@RestController
@RequestMapping(value = "/api/auth", produces = "application/json")
@CrossOrigin("*")
@Tag(name = "Authentication")
public class AuthenticationController {

    private final AuthenticationService authenticationService;

    @PostMapping(value = "/register", consumes = "application/json")
    public ResponseEntity<UserResponse> register(@Valid @RequestBody RegisterRequest request){
        UserResponse newUser = authenticationService.register(request);
        return ResponseEntity.status(201).body(newUser);
    }

    @PostMapping(value = "/login", consumes = "application/json")
    public ResponseEntity<LoginResponse> login (@Valid @RequestBody LoginRequest request){
        LoginResponse userResponse = authenticationService.login(request);
        return ResponseEntity.ok(userResponse);
    }
//
//    @SecurityRequirement(name = "bearerAuth")
//    @PostMapping(value = "/change-password", consumes = "application/json")
//    public ResponseEntity<MessageResponse> changePassword(@Valid @RequestBody ChangePasswordRequest request) {
//        authenticationService.changePassword(request);
//        return ResponseEntity.ok(new MessageResponse("Password changed successfully."));
//    }
//
//    @PostMapping(value = "/forgot-password", consumes = "application/json")
//    public ResponseEntity<MessageResponse> forgotPassword(@Valid @RequestBody ForgotPasswordRequest request) {
//        authenticationService.forgotPassword(request);
//        return ResponseEntity.ok(new MessageResponse("Password reset email sent."));
//    }
//
//    @PostMapping(value = "/reset-password", consumes = "application/json")
//    public ResponseEntity<MessageResponse> resetPassword(@Valid @RequestBody ResetPasswordRequest request) {
//        authenticationService.resetPassword(request);
//        return ResponseEntity.ok(new MessageResponse("Password has been reset successfully."));
//    }
//
//    @SecurityRequirement(name = "bearerAuth")
//    @PostMapping("/logout")
//    public ResponseEntity<MessageResponse> logout() {
//        // FE tự xoá token; BE chỉ confirm
//        return ResponseEntity.ok(new MessageResponse("Logout successfully!"));
//    }
//
//    @GetMapping("/activate")
//    public ResponseEntity<MessageResponse> activateAccount(@RequestParam String token) {
//        authenticationService.activateAccount(token);
//        return ResponseEntity.ok(new MessageResponse("Account activated successfully! You can now login."));
//    }
//
//    @PostMapping(value = "/resend-activation", consumes = "application/json")
//    public ResponseEntity<MessageResponse> resendActivationEmail(@Valid @RequestBody ResendActivationRequest request) {
//        authenticationService.resendActivationEmail(request.getEmail());
//        return ResponseEntity.ok(new MessageResponse("Activation email has been resent. Please check your inbox."));
//    }
}
