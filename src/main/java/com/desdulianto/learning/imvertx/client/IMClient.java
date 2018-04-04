package com.desdulianto.learning.imvertx.client;

import io.vertx.core.Vertx;

public class IMClient {
    public static void main(String []args) {
        Vertx vertx = Vertx.vertx();

        // deploy aplikasi client
        IMClientVerticle imVerticle = new IMClientVerticle();
        vertx.deployVerticle(imVerticle);
    }
}
