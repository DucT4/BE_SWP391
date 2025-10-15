package com.SWP391_02.dto;

import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.constraints.NotBlank;
import lombok.Data;
import java.time.LocalDate;

@Data
public class CreateCampaignRequest {
    @Schema(example = "Recall")
    @NotBlank
    private String type;

    @Schema(example = "Chiến dịch thay cảm biến nhiệt độ VF8")
    @NotBlank
    private String name;

    private String description;
    private LocalDate startDate;
    private LocalDate endDate;
    private String status = "Planned"; // Planned / Active / Closed
}
