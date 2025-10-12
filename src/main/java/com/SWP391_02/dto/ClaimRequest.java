package com.SWP391_02.dto;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import lombok.Data;

@Data
public class ClaimRequest {

    @NotBlank(message = "VIN không được để trống")
    private String vin;

    @NotNull(message = "Service Center ID không được để trống")
    private Long serviceCenterId;

    private String failureDesc;
}
