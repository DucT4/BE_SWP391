package com.SWP391_02.entity;

import jakarta.persistence.*;
import lombok.*;

@Entity
@Table(name = "shipment_lines")
@Getter @Setter @NoArgsConstructor @AllArgsConstructor @Builder
public class ShipmentLine {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "shipment_id")
    private Shipment shipment;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "part_id")
    private Part part;

    @Column(nullable = false)
    private Integer quantity;
}
