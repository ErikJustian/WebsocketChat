package com.desdulianto.learning.imvertx.packet;

import com.fasterxml.jackson.annotation.JsonCreator;
import com.fasterxml.jackson.annotation.JsonProperty;

public class ConversationMessage extends TextMessage {
    private final String from;
    private final String to;

    @JsonCreator
    public ConversationMessage(@JsonProperty("message") String message,
                               @JsonProperty("from") String from,
                               @JsonProperty("to") String to) {
        super(message);
        this.from = from;
        this.to = to;
    }

    public String getFrom() {
        return from;
    }

    public String getTo() {
        return to;
    }

    @Override
    public String toString() {
        return "ConversationMessage{" +
                "from='" + from + '\'' +
                ", to='" + to + '\'' +
                '}';
    }
}
