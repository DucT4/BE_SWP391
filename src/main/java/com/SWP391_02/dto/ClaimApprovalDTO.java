package com.SWP391_02.dto;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class ClaimApprovalDTO {

    @NotNull(message = "Claim ID không được để trống")
    private Long claimId;

    @NotBlank(message = "Decision không được để trống")
    private String decision; // APPROVED / REJECTED

    private String remark;
}
