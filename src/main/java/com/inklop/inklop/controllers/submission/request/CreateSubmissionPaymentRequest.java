package com.inklop.inklop.controllers.submission.request;

import com.inklop.inklop.entities.valueObject.submission.PaymentStatus;

import java.math.BigDecimal;

public record CreateSubmissionPaymentRequest(
        BigDecimal payment,
        BigDecimal paymentReceived,
        Long views,
        Long likes,
        Long comments,
        PaymentStatus paymentStatus
) {}
