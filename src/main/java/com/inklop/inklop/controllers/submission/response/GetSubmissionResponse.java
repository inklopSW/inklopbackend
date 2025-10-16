package com.inklop.inklop.controllers.submission.response;

import com.inklop.inklop.entities.valueObject.submission.SubmissionStatus;

import java.time.LocalDateTime;
import java.util.UUID;

public record GetSubmissionResponse(
        Long id,
        UUID uuid,
        String videoUrl,
        LocalDateTime submittedAt,
        SubmissionStatus submissionStatus,
        GetSubmissionPaymentResponse payments
) {}
