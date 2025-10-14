package com.SWP391_02.entity;

import jakarta.persistence.*;
import lombok.*;
import java.time.LocalDateTime;

@Entity
@Table(name = "quotations")
@Getter @Setter @Builder
@NoArgsConstructor @AllArgsConstructor
public class Quotation {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(name = "repair_id", nullable = false)
    private Long repairId;

    @Column(name = "total_amount", nullable = false)
    private Double totalAmount;

    @Column(length = 300)
    private String note;

    @Column(length = 30)
    private String status; // PENDING / APPROVED / REJECTED

    @Column(name = "created_at")
    private LocalDateTime createdAt;
}
