package com.desdulianto.learning.imvertx.packet;

import com.desdulianto.learning.imvertx.model.User;
import com.fasterxml.jackson.annotation.JsonCreator;
import com.fasterxml.jackson.annotation.JsonProperty;

public class LoginNotification extends ChatMessage {
    private final User user;

    @JsonCreator
    public LoginNotification(@JsonProperty("user") User user) {
        this.user = user;
    }

    public User getUser() {
        return user;
    }

    @Override
    public String toString() {
        return "LoginNotification{" +
                "user=" + user +
                '}';
    }
}
