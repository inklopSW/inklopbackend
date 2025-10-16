package com.inklop.inklop.controllers.campaign.response;

import com.inklop.inklop.entities.valueObject.campaign.Currency;
import com.inklop.inklop.entities.valueObject.campaign.PaymentStatus;

import java.math.BigDecimal;


public record TransaccionComplete(
    String campaignName,
    BigDecimal amount,
    Currency currency,
    String date,
    PaymentStatus status,
    String refNumber
) {
}

