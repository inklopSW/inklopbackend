package com.inklop.inklop.controllers.submission.request;

import com.inklop.inklop.entities.valueObject.submission.AppealStatus;

public record AppealStatusRequest(
    AppealStatus status,
    String adminComment
) {    
}
