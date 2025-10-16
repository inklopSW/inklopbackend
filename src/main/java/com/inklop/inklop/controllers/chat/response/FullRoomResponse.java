package com.inklop.inklop.controllers.chat.response;

import java.time.LocalDateTime;

import com.inklop.inklop.entities.valueObject.chat.ChatStatus;
import com.inklop.inklop.entities.valueObject.chat.RoomType;

public record FullRoomResponse(
    Long idRoom,
    Long idCampaign,
    String imageRoom,
    String bussinessName,
    String title,
    String description,
    RoomType roomType,
    ChatStatus status,
    LocalDateTime createdAt,
    LocalDateTime updatedAt
) {
}