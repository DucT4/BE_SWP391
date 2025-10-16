package com.SWP391_02.entity;

import jakarta.persistence.*;
import lombok.*;
import java.time.LocalDate;
import java.time.LocalDateTime;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
@Entity
@Table(name = "parts_allocations")
public class PartsAllocation {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(name = "allocation_code", nullable = false, unique = true, length = 64)
    private String allocationCode;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "request_id")
    private PartsRequest request;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "from_warehouse_id")
    private Warehouse fromWarehouse;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "to_service_center_id")
    private ServiceCenter toServiceCenter;

    @Column(length = 50)
    private String status;

    @Column(name = "eta_date")
    private LocalDate etaDate;

    @Column(name = "created_at")
    private LocalDateTime createdAt;
}
