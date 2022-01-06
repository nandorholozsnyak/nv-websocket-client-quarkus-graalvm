package com.example;

import com.neovisionaries.ws.client.WebSocket;
import io.quarkus.runtime.Quarkus;
import io.quarkus.runtime.QuarkusApplication;
import io.quarkus.runtime.annotations.QuarkusMain;

import javax.inject.Inject;

@QuarkusMain
public class Main implements QuarkusApplication {

    @Inject
    WebSocket webSocket;

    public static void main(String[] args) {
        Quarkus.run(Main.class, args);
    }

    @Override
    public int run(String... args) throws Exception {
        System.out.println("Is WS connection open: " + webSocket.isOpen());
        return 0;
    }
}
