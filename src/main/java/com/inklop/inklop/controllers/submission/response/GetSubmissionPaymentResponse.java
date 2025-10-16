package com.inklop.inklop.controllers.submission.response;

import com.inklop.inklop.entities.valueObject.submission.PaymentStatus;

import java.math.BigDecimal;

public record GetSubmissionPaymentResponse(
        BigDecimal payment,
        BigDecimal paymentReceived,
        BigDecimal engagement,
        Long views,
        Long likes,
        Long comments,
        PaymentStatus paymentStatus
) {}
