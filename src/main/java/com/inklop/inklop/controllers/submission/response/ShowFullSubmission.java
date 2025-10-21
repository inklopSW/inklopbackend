package com.inklop.inklop.controllers.submission.response;

import java.time.LocalDateTime;

import com.inklop.inklop.entities.valueObject.campaign.PaymentStatus;
import com.inklop.inklop.entities.valueObject.submission.SubmissionStatus;
import com.inklop.inklop.services.scrapper.dto.VideoStatsResponse;

public record ShowFullSubmission(
    Long id,
    SubmissionStatus submissionStatus,
    PaymentStatus paymentStatus,
    LocalDateTime submittedAt,
    String description,
    IncomeDto income,
    VideoStatsResponse videoStats
) {
    
}