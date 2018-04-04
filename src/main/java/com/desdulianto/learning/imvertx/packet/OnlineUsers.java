package com.desdulianto.learning.imvertx.packet;

import com.desdulianto.learning.imvertx.model.User;
import com.fasterxml.jackson.annotation.JsonCreator;
import com.fasterxml.jackson.annotation.JsonProperty;

import java.util.Collection;

public class OnlineUsers extends ChatMessage {
    private final Collection<User> users;

    @JsonCreator
    public OnlineUsers(@JsonProperty("users") Collection<User> users) {
        this.users = users;
    }

    public Collection<User> getUsers() {
        return users;
    }

    @Override
    public String toString() {
        return "OnlineUsers{" +
                "users=" + users +
                '}';
    }
}
