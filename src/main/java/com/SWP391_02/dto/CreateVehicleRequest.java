package com.SWP391_02.dto;

import jakarta.validation.constraints.*;
import java.time.LocalDate;

public record CreateVehicleRequest(
        @NotBlank @Size(max = 32) String vin,
        @NotBlank @Size(max = 80) String model,
        Long customerId,
        LocalDate purchaseDate,
        LocalDate coverageTo
) {}
