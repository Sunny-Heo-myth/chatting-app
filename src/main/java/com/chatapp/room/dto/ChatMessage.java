package com.chatapp.room.dto;


import lombok.Getter;
import lombok.Setter;

@Setter
@Getter
public class ChatMessage {
    private String from;
    private String content;
}
