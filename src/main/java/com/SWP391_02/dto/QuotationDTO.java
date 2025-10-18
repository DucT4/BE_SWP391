package com.SWP391_02.dto;

import lombok.Data;

@Data
public class QuotationDTO {
    private Long repairId;
    private Double totalAmount;
    private String note;
    private String status; // PENDING_APPROVAL, APPROVED, REJECTED
}
