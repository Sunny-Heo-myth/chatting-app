package com.chatapp.api.dto;

import java.time.ZonedDateTime;

public record RoomRegisterRequest(String userName,
                                  String roomName) {
}
