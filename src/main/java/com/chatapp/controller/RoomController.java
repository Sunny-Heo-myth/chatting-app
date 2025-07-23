package com.chatapp.controller;

import org.springframework.web.bind.annotation.*;

import java.time.Instant;
import java.util.List;
import java.util.concurrent.CopyOnWriteArrayList;

@RestController
@RequestMapping("/api/rooms")
@CrossOrigin
public class RoomController {
    private final List<RoomDto> rooms;

    public RoomController() {
        rooms = new CopyOnWriteArrayList<>();
        rooms.add(new RoomDto(Instant.now()));
    }

    @GetMapping
    public List<RoomDto> listRooms() {
        return rooms;
    }

    @PostMapping
    public void createRoom(@RequestBody RoomDto room) {
        rooms.add(room);
    }
}
