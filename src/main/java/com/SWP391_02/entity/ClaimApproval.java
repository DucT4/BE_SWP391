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
    @JoinColumn(name = "approver_id")
    private User approver;

    @Column(nullable = false, length = 40)
    private String level;       // MANAGER / EVM

    @Column(nullable = false, length = 20)
    private String decision;    // APPROVED / REJECTED / FORWARDED

    @Column(length = 200)
    private String remark;

    @Column(name = "decision_at", nullable = false)
    private LocalDateTime decisionAt;

    @PrePersist
    protected void onCreate() {
        decisionAt = LocalDateTime.now();
    }
}
