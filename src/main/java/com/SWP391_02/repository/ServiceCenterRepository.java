package com.SWP391_02.repository;

import com.SWP391_02.entity.ServiceCenters;
import org.springframework.data.jpa.repository.JpaRepository;
import java.util.Optional;

public interface ServiceCenterRepository extends JpaRepository<ServiceCenters, Long> {
    Optional<ServiceCenters> findByCode(String code);

}
