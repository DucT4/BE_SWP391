package com.SWP391_02.dto;
import jakarta.validation.constraints.*;

public record CreateVehicleRequest(
        @NotBlank @Size(min=11, max=17) String vin,
        @NotBlank String model,
        @Min(1990) Integer year,
        @NotBlank String ownerName
) {}