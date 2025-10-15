package com.SWP391_02.entity;

import com.SWP391_02.enums.AssignmentStatus;
import jakarta.persistence.*;
import lombok.*;
import java.time.LocalDateTime;

@Entity
@Table(name = "claim_assignments")
@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class ClaimAssignment {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(name = "claim_id", nullable = false)
    private Long claimId;

    @Column(name = "technician_id")
    private Long technicianId;

    @Column(name = "assigned_by")
    private Long assignedBy;

    @Enumerated(EnumType.STRING)
    @Column(name = "status", length = 40)
    private AssignmentStatus status;

    @Column(name = "assigned_at")
    private LocalDateTime assignedAt;

    @Column(name = "accepted_at")
    private LocalDateTime acceptedAt;

    @Column(name = "started_at")
    private LocalDateTime startedAt;

    @Column(name = "completed_at")
    private LocalDateTime completedAt;

    @Column(name = "note", length = 200)
    private String note;

    // Relationships
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "claim_id", insertable = false, updatable = false)
    private Claim claim;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "technician_id", insertable = false, updatable = false)
    private User technician;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "assigned_by", insertable = false, updatable = false)
    private User assignedByUser;

    @PrePersist
    protected void onCreate() {
        if (assignedAt == null) {
            assignedAt = LocalDateTime.now();
        }
        if (status == null) {
            status = AssignmentStatus.ASSIGNED;
        }
    }
}

