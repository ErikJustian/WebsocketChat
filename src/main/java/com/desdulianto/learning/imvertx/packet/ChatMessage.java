package com.desdulianto.learning.imvertx.packet;

import com.fasterxml.jackson.annotation.JsonCreator;
import com.fasterxml.jackson.annotation.JsonTypeInfo;


// class untuk menampung format json
@JsonTypeInfo(use= JsonTypeInfo.Id.CLASS, include= JsonTypeInfo.As.PROPERTY, property="@class")
public class ChatMessage {
    @JsonCreator
    public ChatMessage() {
    }

    @Override
    public String toString() {
        return "ChatMessage{}";
    }
}
