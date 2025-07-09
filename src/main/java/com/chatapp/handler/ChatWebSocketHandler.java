package com.chatapp.handler;

import com.fasterxml.jackson.databind.ObjectMapper;
import org.springframework.web.socket.CloseStatus;
import org.springframework.web.socket.TextMessage;
import org.springframework.web.socket.WebSocketSession;
import org.springframework.web.socket.handler.AbstractWebSocketHandler;

import java.time.Instant;
import java.util.Map;
import java.util.Set;
import java.util.concurrent.ConcurrentHashMap;

public class ChatWebSocketHandler extends AbstractWebSocketHandler {

    private final Map<String, Set<WebSocketSession>> rooms = new ConcurrentHashMap<>();
    private final ObjectMapper mapper = new ObjectMapper();

    @Override
    public void handleTextMessage(WebSocketSession session, TextMessage message) throws Exception {
        String[] parts = message.getPayload().split("\\|", 3);
        String command = parts[0];
        String room = parts[1];

        switch (command) {
            case "join" -> rooms.computeIfAbsent(room, r -> ConcurrentHashMap.newKeySet()).add(session);
            case "send" -> {
                Message text = mapper.readValue(parts[2], Message.class);
                text.setTimestamp(Instant.now().toString());

                String broadcast = mapper.writeValueAsString(text);
                for (WebSocketSession s : rooms.getOrDefault(room, Set.of())) {
                    if (s.isOpen()) s.sendMessage(new TextMessage(broadcast));
                }
            }
        }
    }

    @Override
    public void afterConnectionClosed(WebSocketSession session, CloseStatus status) {
        rooms.values().forEach(set -> set.remove(session));
    }

}

