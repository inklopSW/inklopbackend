package com.inklop.inklop.controllers.webSocket.response;

import java.time.LocalDateTime;

public record NotificationResponse(
    String image,
    String title,
    String description,
    LocalDateTime time
) {
}