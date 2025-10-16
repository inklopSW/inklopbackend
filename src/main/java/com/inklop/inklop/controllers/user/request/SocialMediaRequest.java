package com.inklop.inklop.controllers.user.request;

import com.inklop.inklop.entities.valueObject.user.Platform;

public record SocialMediaRequest(
    Platform platform,
    String link
) {
    
}
