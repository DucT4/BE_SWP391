package com.SWP391_02.dto;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class PartResponse {
    private Long id;
    private String partNo;
    private String name;
    private Boolean trackSerial;
    private Boolean trackLot;
    private String uom;
}