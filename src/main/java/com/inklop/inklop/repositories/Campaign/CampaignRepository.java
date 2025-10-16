package com.inklop.inklop.repositories.Campaign;

import java.time.LocalDate;
import java.util.List;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import com.inklop.inklop.entities.Campaign.Campaign;
import com.inklop.inklop.entities.valueObject.campaign.CampaignStatus;

@Repository
public interface CampaignRepository extends JpaRepository<Campaign, Long> {
    // Additional query methods can be defined here if needed
    @Query("SELECT c FROM Campaign c WHERE c.campaignStatus = :status")
    List<Campaign> findByStatus(@Param("status") CampaignStatus status);

    List<Campaign> findByBusinessId(Long id);


    @Query("SELECT c FROM Campaign c WHERE c.campaignStatus IN :statuses AND (c.startDate <= :today OR c.endDate <= :today)")
    List<Campaign> findByStatusAndDateBeforeOrEqual(@Param("statuses") List<CampaignStatus> statuses, @Param("today") LocalDate today);


}
 