package com.SWP391_02.entity;

import jakarta.persistence.*;
import lombok.*;

@Entity
@Table(name = "campaign_vins",
        uniqueConstraints = @UniqueConstraint(columnNames = {"campaign_id", "vin"}))
@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class CampaignVin {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(name = "campaign_id", nullable = false)
    private Long campaignId;

    @Column(length = 32, nullable = false)
    private String vin;

    @Column(length = 30)
    private String status; // Planned / Completed / Cancelled
}
