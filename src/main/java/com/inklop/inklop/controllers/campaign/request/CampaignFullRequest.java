package com.inklop.inklop.controllers.campaign.request;

import java.time.LocalDate;
import java.math.BigDecimal;
import java.util.List;

import com.inklop.inklop.entities.valueObject.campaign.CampaignCategoryType;
import com.inklop.inklop.entities.valueObject.campaign.Currency;
import com.inklop.inklop.entities.valueObject.campaign.TypeText;
import com.inklop.inklop.entities.valueObject.user.CreatorType;

public record CampaignFullRequest(
    // Campaign details
    Long businessId,
    String title,
    CreatorType creatorType,
    String logo,
    String description,
    LocalDate startDate,
    LocalDate endDate,
    TypeText typeText,
    String textInfluencer,
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
    Long sm_id,
    Boolean hasTiktok,
    Boolean hasInstagram,
    Boolean hasFacebook
    // Payment details
    

) {
    
    public record CountryDto(
        String country,
        String department
    ) {
        public CountryDto(String country, String department) {
            this.country = country;
            this.department = department;
        }
    }

    public record RequerimentDto(
        String requeriment
    ){
        public RequerimentDto(String requeriment) {
            this.requeriment = requeriment;
        }
    }


}
