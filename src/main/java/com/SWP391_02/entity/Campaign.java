package com.SWP391_02.entity;

import jakarta.persistence.*;
import lombok.*;
import java.time.LocalDate;

@Entity
@Table(name = "campaigns")
@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class Campaign {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    private String type; // Recall / Service
    private String name;
    private String description;
    private LocalDate startDate;
    private LocalDate endDate;
    private String status; // Planned / Active / Closed

    @Column(name = "created_by")
    private Long createdBy;
}
