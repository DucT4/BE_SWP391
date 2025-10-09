package com.SWP391_02.entity;

import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDate;

@Entity
@Table(name="warranty_coverages",
        indexes = {@Index(name="idx_wc_vin", columnList="vin")})
@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class WarrantyCoverage {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(length=17, nullable=false)
    private String vin;

    @Column(length=80, nullable=false)
    private String partCategory; // ví dụ: POWERTRAIN, BATTERY, INFOTAINMENT...

    private LocalDate startDate;
    private LocalDate endDate;   // ngày hết hạn thời gian

    private Integer mileageLimit; // km tối đa, có thể null nếu không giới hạn
    private Integer mileageAtStart; // odometer khi bắt đầu (nếu cần)
}
