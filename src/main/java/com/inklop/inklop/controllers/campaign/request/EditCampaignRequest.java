package com.inklop.inklop.controllers.campaign.request;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.util.List;

import com.inklop.inklop.controllers.campaign.request.CampaignFullRequest.CountryDto;
import com.inklop.inklop.controllers.campaign.request.CampaignFullRequest.RequerimentDto;
import com.inklop.inklop.entities.valueObject.campaign.CampaignCategoryType;
import com.inklop.inklop.entities.valueObject.campaign.Currency;
import com.inklop.inklop.entities.valueObject.campaign.TypeText;
import com.inklop.inklop.entities.valueObject.user.CreatorType;

public record EditCampaignRequest(
    String title,
    CreatorType creatorType,
    String logo,
    String description,
    LocalDate startDate,
    LocalDate endDate,
    TypeText typeText,
    String textInfluencer,
    String stablishmentPlace,
    BigDecimal totalBudget,
    BigDecimal cpm,
    Currency currency,
    BigDecimal maximunPayment,
    BigDecimal minimumPayment,
    // Campaign categories
    CampaignCategoryType category,
    // Campaign countries
    List <CountryDto> countries,
    // Campaign Requeriments
    List <RequerimentDto> requeriments,
    // Social Media
    Boolean hasTiktok,
    Boolean hasInstagram,
    Boolean hasFacebook
) {
}
