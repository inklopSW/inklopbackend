package com.inklop.inklop.controllers.chat.request;

import com.inklop.inklop.entities.valueObject.chat.MessageType;

public record MessageRequestTwo(
    Long idRoom,
    Long idUser,
    String content,
    MessageType type
) {
    
}
