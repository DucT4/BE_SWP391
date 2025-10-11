package com.SWP391_02.dto;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class ClaimStatusHistoryDTO {
    private Long id;
    private String status;
    private String note;
    private Long changedBy;
    private String changedByUserName;
    private LocalDateTime changedAt;
}

