package com.inklop.inklop.controllers.campaign.response;

import java.math.BigDecimal;
import java.util.List;

import com.inklop.inklop.entities.valueObject.campaign.Currency;
import com.inklop.inklop.entities.valueObject.campaign.TypeText;

public record CampaignDetails(
    Long id,
    String title,
    String logo,
    String description,
    Integer durationinDays,
    String type,
    String categoria,
    //social
    Boolean hasTiktok,
    Boolean hasInstagram,
    Boolean hasFacebook,
    // Progreso de la campaña
    Integer percentage,
    BigDecimal total_budget,
    BigDecimal spent_budget,
    // CPM PAGO MAXIMO
    BigDecimal cpm,
    Currency currency,
    BigDecimal minimun_payment,
    BigDecimal maximun_payment,
    Integer maximum_views,
    //Lista de pautas
    List<String> guidelines,
    //else
    TypeText typeText,
    String textInfluencer
) {
    
}
