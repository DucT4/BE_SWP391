package com.SWP391_02.entity;

import jakarta.persistence.*;
import lombok.*;

@Entity
@Table(name = "service_centers")
@Getter @Setter @NoArgsConstructor @AllArgsConstructor @Builder
public class ServiceCenter {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(nullable=false, unique=true, length=100)
    private String code; // Mã TT (ví dụ: SC_HCM01)

    @Column(nullable=false, length=150)
    private String name; // Tên trung tâm

    @Column(length=255)
    private String address; // Địa chỉ

    @Column(length=50)
    private String phone;

    @Column(length=100)
    private String email;

    @Column(length=100)
    private String managerName; // Người phụ trách TT

    @Column(length=50)
    private String status; // ACTIVE / INACTIVE
}
