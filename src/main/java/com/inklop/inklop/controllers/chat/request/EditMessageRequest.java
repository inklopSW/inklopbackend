package com.inklop.inklop.controllers.chat.request;

import com.inklop.inklop.entities.valueObject.chat.MessageType;

public record EditMessageRequest(
    MessageType type,
    String content
) {
    
}
