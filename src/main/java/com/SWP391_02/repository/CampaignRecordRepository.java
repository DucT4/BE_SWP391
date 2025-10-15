package com.SWP391_02.repository;

import com.SWP391_02.entity.CampaignRecord;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;
import java.util.List;

@Repository
public interface CampaignRecordRepository extends JpaRepository<CampaignRecord, Long> {
    List<CampaignRecord> findByCampaignId(Long campaignId);
}
