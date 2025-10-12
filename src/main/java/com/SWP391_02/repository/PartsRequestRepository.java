package com.SWP391_02.repository;

import com.SWP391_02.entity.PartsRequest;
import org.springframework.data.jpa.repository.JpaRepository;
import java.util.List;

public interface PartsRequestRepository extends JpaRepository<PartsRequest, Long> {
    List<PartsRequest> findByServiceCenter_Id(Long serviceCenterId);
    List<PartsRequest> findByStatus(String status);
}
