package com.SWP391_02.entity;


import jakarta.persistence.*;
import lombok.*;

@Entity
@Table(name = "parts")
@Getter @Setter @NoArgsConstructor @AllArgsConstructor @Builder
public class Part {
    @Id @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(name="part_no", nullable=false, unique=true, length=64)
    private String partNo;

    @Column(nullable=false, length=150)
    private String name;

    @Column(name="track_serial")
    private Boolean trackSerial;

    @Column(name="track_lot")
    private Boolean trackLot;

    @Column(length=20)
    private String uom;
}

