package com.desdulianto.learning.imvertx.packet;

import com.fasterxml.jackson.annotation.JsonCreator;
import com.fasterxml.jackson.annotation.JsonProperty;

public class TextMessage extends ChatMessage {
    private final String message;

    @JsonCreator
    public TextMessage(@JsonProperty("message") final String message) {
        super();
        this.message = message;
    }

    public TextMessage() {
        this("");
    }

    public String getMessage() {
        return message;
    }

    @Override
    public String toString() {
        return "TextMessage{" +
                "message='" + message + '\'' +
                '}';
    }
}
