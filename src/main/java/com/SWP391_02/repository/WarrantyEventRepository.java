package com.SWP391_02.repository;
import com.SWP391_02.entity.WarrantyEvent;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;

public interface WarrantyEventRepository extends JpaRepository<WarrantyEvent, Long> {
    Page<WarrantyEvent> findByVinOrderByEventTimeDesc(String vin, Pageable pageable);
}