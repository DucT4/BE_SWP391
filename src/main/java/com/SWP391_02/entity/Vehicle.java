package com.SWP391_02.entity;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.Id;
import jakarta.persistence.Table;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Entity
@Table(name="vehicles")
@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class Vehicle {
    @Id
    @Column(length = 17) // chuẩn VIN
    private String vin;

    @Column(length = 100) private String model;
    private Integer year;
    @Column(length = 100) private String ownerName;
}
