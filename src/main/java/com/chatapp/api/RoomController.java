package com.chatapp.api;

import com.chatapp.api.dto.RoomDto;
import com.chatapp.api.dto.RoomRegisterRequest;
import com.chatapp.exception.DataNotFoundException;
import com.chatapp.room.domain.Room;
import org.springframework.web.bind.annotation.*;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

@RestController
@RequestMapping("room")
public class RoomController {

    public static Map<String, Room> GLOBAL_CHATROOM_MAP = new HashMap<>();

    @PostMapping
    public Room makeRoom(@RequestBody RoomRegisterRequest request) {
        return null;
    }

    @GetMapping("{roomName}")
    public RoomDto getRoom(@PathVariable("roomName") String roomName) {
        if (GLOBAL_CHATROOM_MAP.containsKey(roomName)) {
            Room room = GLOBAL_CHATROOM_MAP.get(roomName);
            return new RoomDto(room.getMaker(), room.getRoomName(), room.getCreatedAt());
        } else {
            throw new DataNotFoundException("Could not found room with name: " + roomName);
        }
    }

    @GetMapping
    public List<RoomDto> getRoomList() {
        return GLOBAL_CHATROOM_MAP.values().stream()
                .map(room -> new RoomDto(room.getMaker(), room.getRoomName(), room.getCreatedAt()))
                .toList();
    }
}
