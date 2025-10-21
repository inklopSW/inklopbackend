package com.inklop.inklop.controllers.submission.response;

import java.time.LocalDateTime;

import com.inklop.inklop.entities.valueObject.submission.AppealStatus;
import com.inklop.inklop.entities.valueObject.submission.TypeAppeal;
import com.inklop.inklop.services.scrapper.dto.VideoStatsResponse;

public record AppealResponse(
    Long id,
    String reason,
    TypeAppeal typeAppeal,
    String adminComment,
    AppealStatus appealStatus,
    LocalDateTime updateAt,
    VideoStatsResponse videoStats
) {
}
