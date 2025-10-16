package com.inklop.inklop.controllers.webSocket;

import lombok.RequiredArgsConstructor;
import org.springframework.messaging.handler.annotation.MessageMapping;
import org.springframework.stereotype.Controller;
import com.inklop.inklop.controllers.chat.request.MessageRequestTwo;
import com.inklop.inklop.services.MessageService;

@Controller
@RequiredArgsConstructor
public class ChatWebSocketController {

    private final MessageService chatService;

    // Recibe mensajes en /app/chat.sendMessage
    @MessageMapping("/chat.sendMessage")
    public void sendMessage(MessageRequestTwo request) {
        chatService.sendMessageTwo(request);
    }
}
