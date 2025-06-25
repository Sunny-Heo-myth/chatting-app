package com.chatapp.room;

import com.chatapp.room.dto.ChatMessage;
import org.springframework.messaging.handler.annotation.MessageMapping;
import org.springframework.messaging.handler.annotation.SendTo;
import org.springframework.stereotype.Controller;

@Controller
public class ChatController {

    @MessageMapping("/send")
    @SendTo("/topic/messages")  // broadcast Clients subscribed to 'topic/messages'
    public ChatMessage send(ChatMessage message) {
        return message;
    }
}
