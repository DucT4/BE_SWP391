package com.SWP391_02.entity;

import com.SWP391_02.enums.WarrantyEventType;
import jakarta.persistence.*;
import lombok.*;
import java.time.LocalDateTime;

@Entity @Table(name="warranty_events",
        indexes = {@Index(name="idx_we_vin_time", columnList="vin,eventTime")})
@Data @Builder @NoArgsConstructor @AllArgsConstructor
public class WarrantyEvent {
    @Id @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(length=17, nullable=false)
    private String vin;

    @Enumerated(EnumType.STRING)
    @Column(length=30, nullable=false)
    private WarrantyEventType type;

    private LocalDateTime eventTime;

    @Column(length=120)
    private String reference; // mã claim, R/O, ticket...

    @Column(length=255)
    private String note;
}
