package com.chatapp.handler;

import com.chatapp.model.RoomManager;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;
import org.springframework.web.socket.CloseStatus;
import org.springframework.web.socket.TextMessage;
import org.springframework.web.socket.WebSocketSession;
import org.springframework.web.socket.handler.TextWebSocketHandler;

import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

@Component
@RequiredArgsConstructor
public class ChatWebSocketHandler extends TextWebSocketHandler {

    private final RoomManager roomManager;
    private final Map<WebSocketSession, String> sessionRooms = new ConcurrentHashMap<>();

    @Override
    public void afterConnectionEstablished(WebSocketSession session) {}

    @Override
    protected void handleTextMessage(WebSocketSession session, TextMessage message) {
        String payload = message.getPayload();
        String[] parts = payload.split("\\|", 4); // format: command|room|name|message
        if (parts.length < 2) return;
        String command = parts[0];
        String room = parts[1];

        switch (command) {
            case "create":
            case "join":
                roomManager.addUserToRoom(room, session);
                sessionRooms.put(session, room);
                broadcastRoomList();
                break;
            case "send":
                if (parts.length < 4) return;
                String name = parts[2];
                String content = parts[3];
                broadcastToRoom(room, String.format("%s: %s", name, content));
                break;
        }
    }

    @Override
    public void afterConnectionClosed(WebSocketSession session, CloseStatus status) {
        String room = sessionRooms.get(session);
        if (room != null) {
            roomManager.removeUserFromRoom(room, session);
            sessionRooms.remove(session);
            broadcastRoomList();
        }
    }

    private void broadcastToRoom(String room, String message) {
        roomManager.getSessions(room).forEach(s -> {
            try {
                s.sendMessage(new TextMessage(message));
            } catch (Exception ignored) {}
        });
    }

    private void broadcastRoomList() {
        String rooms = String.join(",", roomManager.getRooms());
        sessionRooms.keySet().forEach(s -> {
            try {
                s.sendMessage(new TextMessage("rooms|" + rooms));
            } catch (Exception ignored) {}
        });
    }

}

