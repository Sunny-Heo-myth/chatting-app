package com.chatapp.api;

import com.chatapp.api.dto.ChatMessage;
import org.springframework.messaging.handler.annotation.DestinationVariable;
import org.springframework.messaging.handler.annotation.MessageMapping;
import org.springframework.messaging.handler.annotation.SendTo;
import org.springframework.stereotype.Controller;

@Controller
public class ChatController {



    @MessageMapping("/send/{roomNumber}")
    @SendTo("/topic/messages")  // broadcast Clients subscribed to 'topic/messages'
    public ChatMessage send(@DestinationVariable("roomNumber") String roomNumber, ChatMessage message) {
        return message;
    }
}
