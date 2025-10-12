package com.SWP391_02.entity;


import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import java.util.List;

@Entity
@Table(name = "service_centers")
@Data
@AllArgsConstructor
@NoArgsConstructor
public class ServiceCenters {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(nullable = false, unique = true, length = 50)
    private String code;

    @Column(nullable = false, length = 150)
    private String name;

    @Column(length = 200)
    private String address;

    @Column(length = 100)
    private String region;

    @Column(name = "manager_user_id")
    private Long managerUserId;

    // Relationships
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "manager_user_id", insertable = false, updatable = false)
    private User managerUser;

}
