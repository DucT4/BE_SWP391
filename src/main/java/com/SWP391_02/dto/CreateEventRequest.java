// src/main/java/com/SWP391_02/dto/CreateEventRequest.java
package com.SWP391_02.dto;

import com.SWP391_02.enums.WarrantyEventType;
import jakarta.validation.constraints.*;
import java.time.LocalDateTime;

public record CreateEventRequest(
        @NotBlank @Size(min=11, max=17) String vin,
        @NotNull WarrantyEventType type,
        @NotNull LocalDateTime eventTime,
        @Size(max=120) String reference,
        @Size(max=255) String note
) {}
