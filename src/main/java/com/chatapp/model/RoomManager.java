package com.chatapp.model;

import org.springframework.stereotype.Component;
import org.springframework.web.socket.WebSocketSession;

import java.util.*;
import java.util.concurrent.ConcurrentHashMap;

@Component
public class RoomManager {

    private final Map<String, Set<WebSocketSession>> rooms = new ConcurrentHashMap<>();

    public synchronized void addUserToRoom(String room, WebSocketSession session) {
        rooms.computeIfAbsent(room, k -> ConcurrentHashMap.newKeySet()).add(session);
    }

    public synchronized void removeUserFromRoom(String room, WebSocketSession session) {
        if (!rooms.containsKey(room)) return;
        Set<WebSocketSession> sessions = rooms.get(room);
        sessions.remove(session);
        if (sessions.isEmpty()) rooms.remove(room);
    }

    public synchronized List<String> getRooms() {
        return new ArrayList<>(rooms.keySet());
    }

    public synchronized Set<WebSocketSession> getSessions(String room) {
        return rooms.getOrDefault(room, Collections.emptySet());
    }

}
