package com.inklop.inklop.repositories.Campaign;

import org.springframework.stereotype.Repository;

import com.inklop.inklop.entities.Campaign.CampaignRequirements;

import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;

@Repository
public interface CampaignRequirementsRepository extends JpaRepository<CampaignRequirements, Long> {
    // Additional query methods can be defined here if needed
    List<CampaignRequirements> findAllByCampaign_Id(Long id);
    void deleteAllByCampaignId(Long Id);
}
