package com.SWP391_02.entity;

import jakarta.persistence.*;
import lombok.*;
import java.time.LocalDate;

@Entity
@Table(name = "vehicles")
@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class Vehicles {

    @Id
    @Column(length = 32)        // PK là String -> KHÔNG @GeneratedValue
    private String vin;

    @Column(nullable = false, length = 80)
    private String model;

    @Column(name = "customer_id")
    private Long customerId;

    @Column(name = "purchase_date")
    private LocalDate purchaseDate;

    @Column(name = "coverage_to")
    private LocalDate coverageTo;
}
