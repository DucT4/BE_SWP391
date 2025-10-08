package com.SWP391_02.dto;


import com.SWP391_02.enums.Role;
import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotBlank;
import lombok.Data;

@Data
public class RegisterRequest {
    @NotBlank
    private String username;

    @NotBlank
    private String password;

    @Email
    private String email;

    private String phone;  // ✅ Thêm field phone

    private Role role;
    private String adminSecret;
}