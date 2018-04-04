package com.desdulianto.learning.imvertx.server;

import io.vertx.core.Vertx;

public class IMServer {
    public static void main(String []args) {
        Vertx vertx = Vertx.vertx();

        // deploy aplikasi server
        IMVerticle imVerticle = new IMVerticle();
        vertx.deployVerticle(imVerticle);
    }
}
