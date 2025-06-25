package com.chatapp.api.dto;

import java.time.ZonedDateTime;

public record RoomDto(String userName,
                      String roomName,
                      ZonedDateTime createdAt) {
}
