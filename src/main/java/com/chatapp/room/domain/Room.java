package com.chatapp.room.domain;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.Setter;

import java.time.ZonedDateTime;

@Getter
@Setter
@AllArgsConstructor
public class Room {

    private String maker;
    private String roomName;
    private ZonedDateTime createdAt;

}
