package com.example;

import com.neovisionaries.ws.client.WebSocket;
import com.neovisionaries.ws.client.WebSocketFactory;

import javax.enterprise.inject.Produces;


public class WebsocketClientProducer {

    /**
     * This code snippet will be used during build time, Quarkus will augment this as part of its build process
     * and during the Native Image build with GraalVM this method is basically being "executed" and at build time the "connect" phase is happening
     * and the problem with that is one of the underlying class's method is being invoked at that time too: ({@link com.neovisionaries.ws.client.Misc#nextBytes(byte[])}) and this method is using a preinitialized {@link  java.security.SecureRandom} instance which is forbidden by GraalVM at build time, but the Misc class can not be initialized at runtime (https://www.graalvm.org/reference-manual/native-image/JCASecurityServices/#securerandom)
     */
    @Produces
    public WebSocket webSocketProducer() {
        WebSocketFactory factory = new WebSocketFactory().setConnectionTimeout(5000);
        try {
            WebSocket socket = factory.createSocket("wss://steemd.privex.io");
            System.out.println("Connected");
            return socket.connect();
        } catch (Exception e) {
            System.err.println(e);
            System.err.println("Error during creating socket");
        }
        return null;
    }

}
