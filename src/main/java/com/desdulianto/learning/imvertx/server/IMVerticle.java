package com.desdulianto.learning.imvertx.server;

import io.vertx.core.AbstractVerticle;
import io.vertx.core.http.HttpServer;
import io.vertx.core.net.NetServer;
import io.vertx.ext.web.Router;

public class IMVerticle extends AbstractVerticle {
    // start server
    @Override
    public void start() throws Exception {
        // network server
        NetServer imServer = getVertx().createNetServer();

        // handling connection from client
        imServer.connectHandler(socket -> new NetConnectionHandler(getVertx(), socket));

        // listen ke jaringan
        imServer.listen(1286, "localhost", res -> {
            if (res.succeeded()) {
                System.out.println("listening");
            } else {
                System.out.println("failed to bind");
                getVertx().close();
            }
        });

        HttpServer httpServer = getVertx().createHttpServer();
        Router router = Router.router(getVertx());

        router.get("/").handler(context -> {
            context.response().sendFile("home.html");
        });
        router.get("/css/bootstrap.min.css").handler(context -> {
            context.response().sendFile("bootstrap.min.css");
        });
        router.get("/css/bootstrap-grid.min.css").handler(context -> {
            context.response().sendFile("bootstrap-grid.min.css");
        });
        router.get("/css/bootstrap-reboot.min.css").handler(context -> {
            context.response().sendFile("bootstrap-reboot.min.css");
        });
        router.get("/css/animate.css").handler(context -> {
            context.response().sendFile("animate.css");
        });
        router.get("/css/pre.css").handler(context -> {
            context.response().sendFile("pre.css");
        });
        router.get("/css/main.css").handler(context -> {
            context.response().sendFile("main.css");
        });
        router.get("/js/main.js").handler(context -> {
            context.response().sendFile("main.js");
        });
        router.get("/js/WOW.min.js").handler(context -> {
            context.response().sendFile("WOW.min.js");
        });
        router.get("/res/people.png").handler(context -> {
            context.response().sendFile("people.png");
        });
        router.get("/res/geo.png").handler(context -> {
            context.response().sendFile("geo.png");
        });

        httpServer.requestHandler(router::accept);
        httpServer.websocketHandler(socket -> new WSConnectionHandler(getVertx(), socket));
        httpServer.listen(8080);
    }
}
