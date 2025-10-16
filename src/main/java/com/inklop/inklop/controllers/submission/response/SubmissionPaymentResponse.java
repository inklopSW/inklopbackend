package com.inklop.inklop.controllers.submission.response;

import java.math.BigDecimal;

import com.inklop.inklop.entities.valueObject.campaign.Currency;
import com.inklop.inklop.entities.valueObject.campaign.PaymentStatus;


public record SubmissionPaymentResponse(
    Long views,
    BigDecimal engagement,
    PaymentStatus paymentStatus,
    BigDecimal payment,
    BigDecimal paymentReceived,
    Currency currency    

){}