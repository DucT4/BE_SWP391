package com.SWP391_02.repository;

import com.SWP391_02.entity.CampaignVin;
import org.springframework.data.jpa.repository.JpaRepository;
import java.util.List;

public interface CampaignVinRepository extends JpaRepository<CampaignVin, Long> {
    List<CampaignVin> findByCampaignId(Long campaignId);
}
