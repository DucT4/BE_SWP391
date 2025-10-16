package com.SWP391_02.entity;

import jakarta.persistence.*;
import lombok.*;
import java.time.LocalDateTime;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
@Entity
@Table(name = "inspection_reports")
public class InspectionReport {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "claim_id", nullable = false)
    private Claim claim;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "technician_id")
    private User technician;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "staff_id")
    private User staff;

    @Column(name = "summary", columnDefinition = "NVARCHAR(500)")
    private String summary;

    @Column(name = "findings", columnDefinition = "NVARCHAR(MAX)")
    private String findings;

    @Column(name = "images", columnDefinition = "NVARCHAR(MAX)")
    private String images;

    @Column(name = "status", length = 30)
    private String status;

    @Column(name = "inspection_date")
    private LocalDateTime inspectionDate;

    @Column(name = "created_at")
    private LocalDateTime createdAt;
}
