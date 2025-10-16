package com.inklop.inklop.scheduler;

import com.inklop.inklop.services.CampaignService;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;

import java.time.LocalDateTime;


@Component
public class CampaignScheduler {

    private final CampaignService campaignService;

    public CampaignScheduler(CampaignService campaignService) {
        this.campaignService = campaignService;
    }

    // Se ejecuta todos los días a las 23:59
    @Scheduled(cron = "0 59 23 * * ?")
    public void updateCampaignsDaily() {
        campaignService.updateCampaignStatusesToday();
        System.out.println("Campaign statuses updated at " + LocalDateTime.now());
    }

    // Solo para pruebas rápidas
    
}
