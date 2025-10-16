package com.inklop.inklop.services.scrapper.dto;

import com.inklop.inklop.entities.valueObject.user.Platform;

public record ProfileResponse(
    String name,
    String ownerId,
    String nickname,
    Platform platform,
    String link,
    String channel_image
) {
}
