package com.inklop.inklop.controllers.chat.request;

import java.time.LocalDateTime;

public record RoomRequest(
        Long id,
        Long campaignId,
        LocalDateTime createdAt
) {
}
