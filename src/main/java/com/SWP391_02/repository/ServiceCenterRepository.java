package com.SWP391_02.repository;

import com.SWP391_02.entity.ServiceCenter;
import org.springframework.data.jpa.repository.JpaRepository;
import java.util.Optional;

public interface ServiceCenterRepository extends JpaRepository<ServiceCenter, Long> {
    Optional<ServiceCenter> findByCode(String code);
}
