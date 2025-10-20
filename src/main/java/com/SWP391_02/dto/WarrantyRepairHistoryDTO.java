package com.SWP391_02.dto;

import lombok.*;

@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
@Builder
public class WarrantyRepairHistoryDTO {
    private String description;
    private String partsUsed;
    private String status;
    private String repairDate;
}
