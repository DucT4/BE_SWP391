// src/main/java/com/SWP391_02/dto/CreateCoverageRequest.java
package com.SWP391_02.dto;

import jakarta.validation.constraints.*;
import java.time.LocalDate;

public record CreateCoverageRequest(
        @NotBlank @Size(min=11, max=17) String vin,
        @NotBlank @Size(max=80) String partCategory,
        @NotNull LocalDate startDate,
        @NotNull LocalDate endDate,
        @PositiveOrZero(message = "mileageLimit phải >= 0") Integer mileageLimit,
        @PositiveOrZero(message = "mileageAtStart phải >= 0") Integer mileageAtStart
) {}
