package com.SWP391_02.dto;
import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotBlank;
public record ResendActivationRequest(
        @NotBlank @Email String email
) {}