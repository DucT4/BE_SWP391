package com.SWP391_02.entity;

import jakarta.persistence.*;
import lombok.*;
import java.time.LocalDateTime;

@Entity
@Table(name = "warranty_repairs")
@Getter @Setter @Builder
@NoArgsConstructor @AllArgsConstructor
public class WarrantyRepair {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(name = "claim_id", nullable = false)
    private Long claimId;

    @Column(name = "technician_id")
    private Long technicianId;

    @Column(length = 32, nullable = false)
    private String vin;

    @Column(length = 500)
    private String description;

    @Column(name = "parts_used", length = 500)
    private String partsUsed;

    @Column(name = "repair_date")
    private LocalDateTime repairDate;

    @Column(length = 30)
    private String status; // IN_PROGRESS / COMPLETED / CANCELLED
}
