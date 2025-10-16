package com.SWP391_02.entity;

import jakarta.persistence.*;
import lombok.*;

import java.math.BigDecimal;

@Entity
@Table(name = "claim_parts")
@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class ClaimPart {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "claim_id", nullable = false)
    private Claim claim;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "part_id", nullable = false)
    private Part part;

    @Column(nullable = false, precision = 12, scale = 2)
    private BigDecimal qty;

    @Column(nullable = false)
    private Boolean planned = true;

    @Column(name = "serial_no", length = 100)
    private String serialNo;

    @Column(name = "lot_no", length = 50)
    private String lotNo;
}

