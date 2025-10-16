package com.SWP391_02.repository;

import com.SWP391_02.entity.CampaignJob;
import com.SWP391_02.entity.Campaign;
import com.SWP391_02.entity.User;
import com.SWP391_02.entity.Vehicles;
import com.SWP391_02.enums.CampaignJobStatus;
import java.util.List;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface CampaignJobRepository extends JpaRepository<CampaignJob, Long> {
    List<CampaignJob> findByCampaign(Campaign campaign);
    List<CampaignJob> findByVehicle(Vehicles vehicle);
    List<CampaignJob> findByTechnician(User technician);
    List<CampaignJob> findByStatus(CampaignJobStatus status);
    List<CampaignJob> findByCampaignAndStatus(Campaign campaign, CampaignJobStatus status);
    List<CampaignJob> findByCampaignAndTechnician(Campaign campaign, User technician);
    List<CampaignJob> findByVehicleAndStatus(Vehicles vehicle, CampaignJobStatus status);
    List<CampaignJob> findByTechnicianAndStatus(User technician, CampaignJobStatus status);
}
