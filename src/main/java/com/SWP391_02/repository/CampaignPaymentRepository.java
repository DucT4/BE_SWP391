package com.SWP391_02.repository;

import com.SWP391_02.entity.CampaignPayment;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface CampaignPaymentRepository  extends JpaRepository<CampaignPayment, Long> {
}
