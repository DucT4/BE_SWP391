package com.SWP391_02.entity;

import com.SWP391_02.enums.PaymentMethod;
import com.SWP391_02.enums.PaymentStatus;
import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;

@Entity
@Table(name = "campaign_payments")
@Data
@AllArgsConstructor
@NoArgsConstructor
public class CampaignPayment {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @ManyToOne
    @JoinColumn(name = "campaign_record_id", nullable = false)
    private CampaignRecord campaignRecord;

    @ManyToOne
    @JoinColumn(name = "customer_id")
    private Customer customer;

    private Double amount;
    private String currency;

    @Enumerated(EnumType.STRING)
    @Column(name = "payment_status")
    private PaymentStatus status;

    @Enumerated(EnumType.STRING)
    @Column(name = "payment_method")
    private PaymentMethod method;

    @Column(name = "payment_ref_no")
    private String paymentRefNo;

    @Column(name = "payment_date")
    private LocalDateTime paymentDate;

    @ManyToOne
    @JoinColumn(name = "received_by")
    private User receivedBy;

    private String note;

    @Column(name = "created_at")
    private LocalDateTime createdAt = LocalDateTime.now();



}
