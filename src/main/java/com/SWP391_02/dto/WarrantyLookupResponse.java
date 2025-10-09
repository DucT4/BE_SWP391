package com.SWP391_02.dto;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDate;
import java.util.List;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class WarrantyLookupResponse {
    private String vin;
    private boolean active;                // còn hiệu lực (ít nhất một coverage active theo ngày)
    private LocalDate asOfDate;            // ngày tra cứu
    private List<CoverageDTO> activeCoverages;
    private List<String> warnings;
}

