package com.SWP391_02.repository;

import com.SWP391_02.entity.WarrantyRepair;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;

public interface WarrantyRepairRepository extends JpaRepository<WarrantyRepair, Long> {
    List<WarrantyRepair> findByVinOrderByRepairDateDesc(String vin);
}
