package com.SWP391_02.dto;
public record HistoryEventDTO(
        String type,
        String reference,
        String note,
        String eventTime
) {}
