package com.SWP391_02.dto;

import lombok.*;
import jakarta.validation.constraints.*;
import java.time.LocalDate;

@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class CampaignRecordRequest {

    @NotBlank
    private String vin;

    @NotNull
    private LocalDate performedDate;

    private String workDescription;
    private String technicianName;
}
