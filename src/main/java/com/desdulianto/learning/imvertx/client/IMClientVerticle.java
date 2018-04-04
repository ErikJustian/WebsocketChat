package com.desdulianto.learning.imvertx.client;

import io.vertx.core.AbstractVerticle;
import io.vertx.core.Future;
import io.vertx.core.json.JsonObject;
import io.vertx.core.net.NetClient;
import io.vertx.core.net.NetSocket;

import java.util.function.Consumer;


// verticle client
public class IMClientVerticle extends AbstractVerticle {
    private NetClient client;
    private NetSocket socket;

    @Override
    public void start() throws Exception {
         // connect ke server
        client = getVertx().createNetClient();

        client.connect(1286, "localhost", res -> {
            if (res.succeeded()) {
                socket = res.result();

                // incoming message handler
                // receive message from network, then send it to the eventbus
                receiveMessage();

                // outgoing message handler
                // read from eventbus then send it through network
                sendMessage();
            } else {
                System.out.println("Unable to connect");
                getVertx().close();
            }
        });
    }

    /**
     * receive message from network and publish it through the eventbus
     */
    private void receiveMessage() {
        socket.handler(buffer -> {
            getVertx().eventBus().publish("incoming", buffer.toJsonObject());
        });
    }

    /**
     * receive message from eventbus and send it trought the network
     */
    private void sendMessage() {
        getVertx().eventBus().consumer("outgoing").handler(objectMessage -> {
            socket.write(JsonObject.mapFrom(objectMessage.body()).toBuffer());
        });
    }

    /**
     * handle incoming message received from the network
     * @param consumer consumer code
     */
    public void incomingMessageHandler(Consumer<JsonObject> consumer) {
        getVertx().eventBus().consumer("incoming").handler(objectMessage -> {
            consumer.accept(JsonObject.mapFrom(objectMessage.body()));
        });
    }

    /**
     * handle outgoing message to the network
     * @param message json message to send
     */
    public void outgoingMessageHandler(JsonObject message) {
        getVertx().eventBus().publish("outgoing", message);
    }

    public void shutdown() {
        getVertx().close();
    }
}
