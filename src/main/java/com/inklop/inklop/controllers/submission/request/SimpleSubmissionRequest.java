package com.inklop.inklop.controllers.submission.request;

public record SimpleSubmissionRequest(
    Long idSocialMedia,
    Long idCampaign,
    String videoUrl
) {
    
}
