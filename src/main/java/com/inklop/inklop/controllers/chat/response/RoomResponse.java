package com.inklop.inklop.controllers.chat.response;

import java.time.LocalDateTime;

public record RoomResponse(
        Long id,
        Long campaignId,
        LocalDateTime createdAt
) {
}
