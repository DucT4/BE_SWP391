package com.SWP391_02.repository;

import com.SWP391_02.entity.ShipmentLine;
import org.springframework.data.jpa.repository.JpaRepository;
import java.util.List;

public interface ShipmentLineRepository extends JpaRepository<ShipmentLine, Long> {
    List<ShipmentLine> findByShipment_Id(Long shipmentId);
}
