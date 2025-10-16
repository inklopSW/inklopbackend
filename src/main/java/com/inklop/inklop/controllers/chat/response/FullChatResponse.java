package com.inklop.inklop.controllers.chat.response;

import java.time.LocalDateTime;

import com.inklop.inklop.entities.valueObject.chat.ChatStatus;
import com.inklop.inklop.entities.valueObject.chat.MessageType;

public record FullChatResponse(
    Long idChat,
    String userAvatar,
    String userName,
    ChatStatus status,
    MessageType type,
    String content,
    LocalDateTime sentAt,
    LocalDateTime updatedAt
) {
}
