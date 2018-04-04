package com.desdulianto.learning.imvertx.server;

import com.desdulianto.learning.imvertx.model.User;
import com.desdulianto.learning.imvertx.packet.ChatMessage;
import com.desdulianto.learning.imvertx.packet.ConversationMessage;
import com.desdulianto.learning.imvertx.packet.ListOnlineUsersMessage;
import com.desdulianto.learning.imvertx.packet.LoginMessage;
import com.desdulianto.learning.imvertx.packet.LoginNotification;
import com.desdulianto.learning.imvertx.packet.LogoutNotification;
import com.desdulianto.learning.imvertx.packet.OnlineUsers;
import com.desdulianto.learning.imvertx.packet.TextMessage;
import io.vertx.core.Vertx;
import io.vertx.core.buffer.Buffer;
import io.vertx.core.http.ServerWebSocket;
import io.vertx.core.json.JsonObject;
import io.vertx.core.shareddata.LocalMap;

import java.util.Collection;
import java.util.Optional;

public class WSConnectionHandler {
    private final Vertx vertx;
    private final ServerWebSocket socket;
    private User user;

    public WSConnectionHandler(Vertx vertx, ServerWebSocket socket) {
        this.vertx = vertx;
        this.socket = socket;
        this.user = null;

        // read message from network
        this.socket.handler(this::receiveMessage);

        // close connection handler
        this.socket.closeHandler(this::close);
    }

    private boolean isOnline() {
        return user != null;
    }

    private Optional<ChatMessage> getMessage(Buffer buffer) {
        JsonObject json = buffer.toJsonObject();
        try {
            return Optional.of(json.mapTo(ChatMessage.class));
        } catch (Exception e){
            System.out.println(e);
            return Optional.empty();
        }
    }

    /**
     * process message from the network
     * @param buffer incoming message from the network
     */
    private void receiveMessage(Buffer buffer) {
        ChatMessage message = getMessage(buffer).orElse(new ChatMessage());
        if (isOnline()) {
            vertx.eventBus().publish("user/" + "dixon", JsonObject.mapFrom(message));
            chatMessageProcess(message);
        } else if (message instanceof LoginMessage) {
            if (! login((LoginMessage) message)) {
                sendToClient(new TextMessage("already online"));
            } else {
                sendToClient(new TextMessage(user.getUsername() + " becomes online"));
            }
        } else {
            sendToClient(new TextMessage("Invalid Message Format"));
        }
    }

    private void chatMessageProcess(ChatMessage message) {
        if (message instanceof ConversationMessage) {
            handleConversation((ConversationMessage) message);
        } else if (message instanceof ListOnlineUsersMessage) {
            sendOnlineUsers();
        }
    }

    /**
     * subscribe to address
     * @param address event bus address
     */
    private void subscribeTo(String address) {
        vertx.eventBus().consumer(address).handler(objectMessage -> {
            sendToClient(JsonObject.mapFrom(objectMessage.body()).mapTo(ChatMessage.class));
        });
    }

    private boolean login(LoginMessage m) {
        LocalMap<String, User> map = vertx.sharedData().getLocalMap("users");
        User user = new User(m.getUsername());
        if (map.putIfAbsent(m.getUsername(), user) == null) {
            this.user = user;

            //subscribe to broadcast
            subscribeTo("broadcast");

            // register handler to user/<username> to send the message to network client
            subscribeTo("user/" + m.getUsername());

            // broadcast login
            broadcastMessage(new LoginNotification(this.user));
        }

        return this.user != null;
    }

    private boolean logout() {
        if (isOnline()) {
            // shared data of online users
            LocalMap<String, User> map = vertx.sharedData().getLocalMap("users");
            map.remove(user.getUsername());

            // broadcast logout
            broadcastMessage(new LogoutNotification(this.user));
            return true;
        }
        return false;
    }

    /**
     * handle message
     * @param message message
     */
    private void handleConversation(ConversationMessage message) {
        // only handle message when online and the sender is valid
        if (isOnline() && message.getFrom().equals(user.getUsername())) {
            sendMessage(new User(message.getTo()), message);
        }
    }

    private void sendOnlineUsers() {
        if (isOnline()) {
            LocalMap<String, User> map = vertx.sharedData().getLocalMap("users");
            Collection<User> users = map.values();
            users.remove(this.user);
            sendMessageToSelf(new OnlineUsers(users));
        }
    }

    private void sendMessage(User user, ChatMessage message) {
        vertx.eventBus().publish("user/" + user.getUsername(), JsonObject.mapFrom(message));
    }

    private void sendMessageToSelf(ChatMessage message) {
        sendMessage(this.user, message);
    }

    private void broadcastMessage(ChatMessage message) {
        System.out.println(message.getClass().getName());
        vertx.eventBus().publish("broadcast", JsonObject.mapFrom(message));
    }

    private void close(Void aVoid) {
        if (logout()) {
            System.out.println(user.getUsername() + " becomes offline");
        }
        System.out.println("client disconnect from " + socket.remoteAddress());
    }

    private void sendToClient(ChatMessage message) {
        socket.write(JsonObject.mapFrom(message).toBuffer());
    }
}
