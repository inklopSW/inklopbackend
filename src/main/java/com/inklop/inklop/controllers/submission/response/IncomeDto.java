package com.inklop.inklop.controllers.submission.response;

import java.math.BigDecimal;

import com.inklop.inklop.entities.valueObject.campaign.Currency;

public record IncomeDto (
    BigDecimal money,
    Currency currency,
    String title,
    String image
){
}
