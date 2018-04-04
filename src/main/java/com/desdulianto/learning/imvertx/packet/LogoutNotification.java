package com.desdulianto.learning.imvertx.packet;

import com.desdulianto.learning.imvertx.model.User;
import com.fasterxml.jackson.annotation.JsonCreator;
import com.fasterxml.jackson.annotation.JsonProperty;

public class LogoutNotification extends ChatMessage {
    private final User user;

    @JsonCreator
    public LogoutNotification(@JsonProperty("user") User user) {
        this.user = user;
    }

    public User getUser() {
        return user;
    }

    @Override
    public String toString() {
        return "LogoutNotification{" +
                "user=" + user +
                '}';
    }
}
