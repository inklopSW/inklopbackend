package com.inklop.inklop.controllers.submission.request;

import com.inklop.inklop.entities.valueObject.submission.TypeAppeal;

public record AppealRequest(
    Long submissionId,
    String reason,
    TypeAppeal typeAppeal
) {
}
