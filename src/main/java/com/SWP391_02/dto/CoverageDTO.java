package com.SWP391_02.dto;

import lombok.*;
import java.time.LocalDate;
import java.util.List;

@Data @Builder @NoArgsConstructor @AllArgsConstructor
public class CoverageDTO {
    private String partCategory;
    private LocalDate startDate;
    private LocalDate endDate;
    private Integer mileageLimit;
}
