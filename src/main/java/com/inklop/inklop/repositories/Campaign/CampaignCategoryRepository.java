package com.inklop.inklop.repositories.Campaign;

import org.springframework.stereotype.Repository;

import com.inklop.inklop.entities.Campaign.CampaignCategory;

import java.util.List;

import org.springframework.data.jpa.repository.JpaRepository;

@Repository
public interface CampaignCategoryRepository extends JpaRepository<CampaignCategory, Long> {
    // Additional query methods can be defined here if needed
    List<CampaignCategory> findAllByCampaign_Id(Long campaignId);
}
