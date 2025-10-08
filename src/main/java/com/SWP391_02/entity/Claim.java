package com.SWP391_02.entity;

import com.SWP391_02.enums.ApprovalLevel;
import com.SWP391_02.enums.ClaimStatus;
import jakarta.persistence.*;
import lombok.*;
import java.time.LocalDateTime;
import java.util.List;

@Entity
@Table(name = "claims")
@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class Claim {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(nullable = false)
    private String vin;

    @Column(name = "opened_by", nullable = false)
    private Long openedBy;

    @Column(name = "service_center_id", nullable = false)
    private Long serviceCenterId;

    @Enumerated(EnumType.STRING)
    @Column(name = "status", nullable = false)
    private ClaimStatus status;

    @Column(name = "failure_desc", columnDefinition = "TEXT")
    private String failureDesc;

    @Enumerated(EnumType.STRING)
    @Column(name = "approval_level")
    private ApprovalLevel approvalLevel;

    @Column(name = "created_at", nullable = false)
    private LocalDateTime createdAt;

    @Column(name = "updated_at")
    private LocalDateTime updatedAt;

    // Relationships
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "vin", insertable = false, updatable = false)
    private Vehicles vehicle;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "opened_by", insertable = false, updatable = false)
    private User openedByUser;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "service_center_id", insertable = false, updatable = false)
    private ServiceCenters serviceCenter;


    @PrePersist
    protected void onCreate() {
        createdAt = LocalDateTime.now();
    }

    @PreUpdate
    protected void onUpdate() {
        updatedAt = LocalDateTime.now();
    }
}
