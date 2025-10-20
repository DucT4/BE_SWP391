package com.SWP391_02.repository;

import com.SWP391_02.entity.Claim;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface ClaimRepository extends JpaRepository<Claim, Long> {
    List<Claim> findByOpenedBy(Long openedBy);
    List<Claim> findByServiceCenterId(Long serviceCenterId);
}
