package com.SWP391_02.repository;

import com.SWP391_02.entity.Shipment;
import org.springframework.data.jpa.repository.JpaRepository;
import java.util.List;

public interface ShipmentRepository extends JpaRepository<Shipment, Long> {
    List<Shipment> findByStatus(String status);
    List<Shipment> findByToServiceCenter_Id(Long serviceCenterId);
}
