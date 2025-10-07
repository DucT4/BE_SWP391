package com.SWP391_02.dto;

import jakarta.validation.constraints.NotBlank;

public record PartUpdateRequest(
        @NotBlank String name,
        @NotBlank String uom,
        Boolean trackSerial,
        Boolean trackLot
) {}

