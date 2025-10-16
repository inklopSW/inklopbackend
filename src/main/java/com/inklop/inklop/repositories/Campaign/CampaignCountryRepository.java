package com.inklop.inklop.repositories.Campaign;

import org.springframework.stereotype.Repository;
import com.inklop.inklop.entities.Campaign.CampaignCountry;
import org.springframework.data.jpa.repository.JpaRepository;

@Repository
public interface CampaignCountryRepository extends JpaRepository<CampaignCountry, Long>{
    void deleteAllByCampaignId(Long Id);
}
