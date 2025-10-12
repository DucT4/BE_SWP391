package com.SWP391_02.entity;

import jakarta.persistence.*;
import lombok.*;

@Entity
@Table(name = "warehouses")
@Getter @Setter @NoArgsConstructor @AllArgsConstructor @Builder
public class Warehouse {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(nullable=false, unique=true, length=100)
    private String code; // Mã kho, ví dụ: WH_HCM01

    @Column(nullable=false, length=150)
    private String name; // Tên kho

    @Column(length=255)
    private String address; // Địa chỉ kho

    @Column(length=50)
    private String phone;

    @Column(length=100)
    private String email;

    @Column(length=100)
    private String managerName; // Người phụ trách kho

    @Column(length=50)
    private String status; // ACTIVE / INACTIVE
}
