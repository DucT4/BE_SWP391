package com.SWP391_02.dto;

import java.math.BigDecimal;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

/**
 * DTO dùng cho báo cáo tổng hợp doanh thu và trạng thái thanh toán theo chiến dịch.
 */
@Data
@NoArgsConstructor
@AllArgsConstructor

public class ReportDTO {
    /**
     * Id của chiến dịch cần báo cáo.
     */
    private Long campaignId;
    /**
     * Tổng số tiền đã thanh toán cho chiến dịch.
     */
    private BigDecimal totalAmount;
    /**
     * Số lượng giao dịch thanh toán của chiến dịch.
     */
    private int paymentCount;
    /**
     * Tổng hợp trạng thái các giao dịch thanh toán (Pending, Completed, ...).
     */
    private String statusSummary;
}
