package com.inklop.inklop.repositories.Campaign;

import com.inklop.inklop.entities.Campaign.CampaignPayment;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface CampaignPaymentRepository extends JpaRepository<CampaignPayment,Long>{
}