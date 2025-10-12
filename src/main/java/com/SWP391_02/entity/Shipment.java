package com.SWP391_02.entity;

import jakarta.persistence.*;
import lombok.*;
import java.time.LocalDate;

@Entity
@Table(name = "shipments")
@Getter @Setter @NoArgsConstructor @AllArgsConstructor @Builder
public class Shipment {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(name = "shipment_code", nullable = false, unique = true, length = 64)
    private String shipmentCode;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "allocation_id")
    private PartsAllocation allocation;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "from_warehouse_id")
    private Warehouse fromWarehouse;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "to_service_center_id")
    private ServiceCenter toServiceCenter;

    @Column(length = 50)
    private String status; // CREATED / SHIPPED / DELIVERED

    @Column(name = "ship_date")
    private LocalDate shipDate;

    @Column(name = "delivered_date")
    private LocalDate deliveredDate;
}
