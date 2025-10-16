package com.inklop.inklop.controllers.campaign.response;

import java.math.BigDecimal;
import java.time.LocalDate;

import com.inklop.inklop.entities.valueObject.campaign.CampaignStatus;


public record ShortCampaignResponse(
    Long id,
    String tittle,
    String logo,
    CampaignStatus status,
    BigDecimal cpm,
    BigDecimal totalBudget,
    BigDecimal spentBudget,
    Integer percentage,
    String type,
    String category,
    Boolean hasTiktok,
    Boolean hasInstagram,
    Boolean hasFacebook,
    LocalDate startDate,
    String businessName
) { 
}
