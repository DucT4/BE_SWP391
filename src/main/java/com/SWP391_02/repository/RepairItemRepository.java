package com.SWP391_02.repository;

import com.SWP391_02.entity.RepairItem;
import org.springframework.data.jpa.repository.JpaRepository;
import java.util.List;

public interface RepairItemRepository extends JpaRepository<RepairItem, Long> {
    List<RepairItem> findByClaimId(Long claimId);
}
