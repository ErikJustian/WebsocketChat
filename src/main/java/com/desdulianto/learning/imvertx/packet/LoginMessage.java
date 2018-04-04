package com.desdulianto.learning.imvertx.packet;

import com.fasterxml.jackson.annotation.JsonCreator;
import com.fasterxml.jackson.annotation.JsonProperty;

public class LoginMessage extends ChatMessage {
    private final String username;
    private final String password;

    @JsonCreator
    public LoginMessage(@JsonProperty("username") String username,
                        @JsonProperty("password") String password) {
        this.username = username;
        this.password = password;
    }

    public String getUsername() {
        return username;
    }

    public String getPassword() {
        return password;
    }

    @Override
    public String toString() {
        return "LoginMessage{" +
                "username='" + username + '\'' +
                ", password='" + password + '\'' +
                '}';
    }
}
