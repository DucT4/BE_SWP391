package com.SWP391_02.repository;

import com.SWP391_02.entity.PartsAllocation;
import org.springframework.data.jpa.repository.JpaRepository;
import java.util.List;

public interface PartsAllocationRepository extends JpaRepository<PartsAllocation, Long> {
    List<PartsAllocation> findByStatus(String status);
    List<PartsAllocation> findByToServiceCenter_Id(Long serviceCenterId);
}
