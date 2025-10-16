package com.inklop.inklop.controllers.user.response;

import com.inklop.inklop.entities.valueObject.user.Platform;

public record SocialMediaResponse(
    Long id,
    Platform platform,
    String name_account,
    String nickname,
    String avatar,
    String link
) {
    
}
