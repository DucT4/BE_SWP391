package com.SWP391_02.entity;

import jakarta.persistence.*;
import lombok.*;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.List;

@Entity
@Table(name = "campaign_records")
@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class CampaignRecord {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(name = "campaign_id", nullable = false)
    private Long campaignId;

    @Column(length = 32, nullable = false)
    private String vin;

    @Column(name = "staff_id")
    private Long staffId;

    @Column(name = "performed_date", nullable = false)
    private LocalDate performedDate;

    @Column(name = "work_description", length = 255)
    private String workDescription;

    @Column(name = "technician_name", length = 100)
    private String technicianName;

    @Column(name = "created_at")
    private LocalDateTime createdAt = LocalDateTime.now();

    @OneToMany(mappedBy = "campaignRecord")
    private List<CampaignPayment> campaignPayments;

}
