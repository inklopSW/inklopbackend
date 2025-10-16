package com.inklop.inklop.controllers.submission.response;

import com.inklop.inklop.entities.valueObject.submission.SubmissionStatus;

public record SubmissionResponse (
    Long id,
    String videoUrl,
    SubmissionStatus submissionStatus,
    Integer percentage,
    String description
){
}
