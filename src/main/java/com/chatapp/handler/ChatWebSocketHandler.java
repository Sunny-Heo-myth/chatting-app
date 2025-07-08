package com.chatapp.handler;

import org.springframework.web.socket.CloseStatus;
import org.springframework.web.socket.TextMessage;
import org.springframework.web.socket.WebSocketSession;
import org.springframework.web.socket.handler.TextWebSocketHandler;

import java.util.Map;
import java.util.Set;
import java.util.concurrent.ConcurrentHashMap;

public class ChatWebSocketHandler extends TextWebSocketHandler {
    // Map<roomName, Set<Session>>
    private final Map<String, Set<WebSocketSession>> rooms = new ConcurrentHashMap<>();

    @Override
    public void handleTextMessage(WebSocketSession session, TextMessage message) throws Exception {
        String[] parts = message.getPayload().split("\\|", 4);
        String command = parts[0];

        switch (command) {
            case "join" -> {
                String room = parts[1];
                rooms.computeIfAbsent(room, r -> ConcurrentHashMap.newKeySet()).add(session);
            }
            case "send" -> {
                String room = parts[1];
                String name = parts[2];
                String content = parts[3];
                String fullMessage = name + ": " + content;

                if (rooms.containsKey(room)) {
                    for (WebSocketSession s : rooms.get(room)) {
                        if (s.isOpen()) s.sendMessage(new TextMessage(fullMessage));
                    }
                }
            }
        }
    }

    @Override
    public void afterConnectionClosed(WebSocketSession session, CloseStatus status) {
        for (Set<WebSocketSession> room : rooms.values()) {
            room.remove(session);
        }
    }
}
