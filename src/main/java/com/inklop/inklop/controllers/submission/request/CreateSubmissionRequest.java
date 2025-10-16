package com.inklop.inklop.controllers.submission.request;

import java.util.UUID;

public record CreateSubmissionRequest(
        UUID creatorId,
        UUID campaignId,
        String videoUrl,
        CreateSubmissionPaymentRequest payments
) {
        
}
