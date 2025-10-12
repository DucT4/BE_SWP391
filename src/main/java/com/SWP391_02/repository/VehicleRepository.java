package com.SWP391_02.repository;

import com.SWP391_02.entity.Vehicles;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.Optional;

@Repository
public interface VehicleRepository extends JpaRepository<Vehicles, String> {

    Optional<Vehicles> findByVin(String vin);

    boolean existsByVin(String vin);
}
