package com.SWP391_02.entity;

import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import java.time.LocalDateTime;

@Entity
@Table(name = "claim_approvals")
@Data
@NoArgsConstructor
@AllArgsConstructor
public class ClaimApproval {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "claim_id", nullable = false)
    private Claim claim;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "approver_id", nullable = false)
    private User approver;

    @Column(nullable = false)
    private String level;       // MANAGER / EVM

    @Column(nullable = false)
    private String decision;    // APPROVED / REJECTED / FORWARDED

    @Column(columnDefinition = "TEXT")
    private String remark;

    @Column(name = "decision_at", nullable = false)
    private LocalDateTime decisionAt;

    @PrePersist
    protected void onCreate() {
        decisionAt = LocalDateTime.now();
    }
}
