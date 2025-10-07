package com.SWP391_02.dto;

import jakarta.validation.constraints.NotBlank;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class AddPartRequest {

    @NotBlank
    private String partNo;

    @NotBlank
    private String name;

    @NotBlank
    private String uom;          // EA, M, L...

    private Boolean trackSerial; // có thể null -> sẽ default false
    private Boolean trackLot;    // có thể null -> sẽ default false ✅ Sửa ở đây
}
