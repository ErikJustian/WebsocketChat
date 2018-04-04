package com.desdulianto.learning.imvertx.client;

import com.desdulianto.learning.imvertx.packet.TextMessage;
import io.vertx.core.AbstractVerticle;
import io.vertx.core.eventbus.EventBus;
import io.vertx.core.json.JsonObject;
import io.vertx.core.net.NetSocket;

import java.util.Scanner;

// verticle interactive untuk menerima input dari user
public class IMInteractiveVerticle extends AbstractVerticle {
    private EventBus eventBus;
    private final NetSocket socket;

    public IMInteractiveVerticle(NetSocket socket) {
        this.socket = socket;
    }

    @Override
    public void start() throws Exception {
        eventBus = getVertx().eventBus();
        Scanner input = new Scanner(System.in);
        while (true) {
            System.out.print("Message: ");
            String message = input.nextLine();

            if (message.equals("/quit")) break;

            socket.write(JsonObject.mapFrom(new TextMessage(message)).toBuffer());
        }

        // announce bahwa proses udah selesai, matikan verticle
        eventBus.publish("system", "stop");
        getVertx().close();
    }
}
