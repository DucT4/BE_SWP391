package com.SWP391_02.repository;

import com.SWP391_02.entity.WarrantyRepair;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import java.util.List;

@Repository
public interface WarrantyRepairRepository extends JpaRepository<WarrantyRepair, Long> {

    // ✅ Dùng Pageable cho API tra cứu /history
    Page<WarrantyRepair> findByVinContainingIgnoreCase(String vin, Pageable pageable);

    // ✅ Lấy danh sách công việc theo technician
    List<WarrantyRepair> findByTechnicianId(Long technicianId);

    // ✅ Thêm mới cho SC Manager: Lấy danh sách công việc theo Service Center
    @Query(value = """
        SELECT r.* 
        FROM warranty_repairs r
        JOIN claims c ON r.claim_id = c.id
        WHERE c.service_center_id = :serviceCenterId
    """, nativeQuery = true)
    List<WarrantyRepair> findByServiceCenterId(@Param("serviceCenterId") Long serviceCenterId);
}
