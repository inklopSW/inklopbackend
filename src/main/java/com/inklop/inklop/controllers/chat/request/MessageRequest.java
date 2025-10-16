package com.inklop.inklop.controllers.chat.request;

import com.inklop.inklop.entities.valueObject.user.UserRole;

import java.time.LocalDateTime;

public record MessageRequest(
        Long id,
        Long userId,
        String content,
        UserRole role,
        LocalDateTime sentAt
) {
}
