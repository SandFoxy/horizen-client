package ru.sandfoxy.horizen.utils;

import javax.websocket.*;
import java.net.URI;

@ClientEndpoint
public class Tracker {
    private static Session session;

    public static void connect(String uri) {
        try {
            WebSocketContainer container = ContainerProvider.getWebSocketContainer();
            session = container.connectToServer(Tracker.class, new URI(uri));
        } catch (Exception e) {
            System.out.println("Connection failed: " + e.getMessage());
        }
    }

    public static void send(String nickname, String ip) {
        try {
            if (session != null && session.isOpen()) {
                session.getAsyncRemote().sendText(nickname + ":" + ip);
            }
        } catch (Exception e) {
            System.out.println("Send failed: " + e.getMessage());
        }
    }

    @OnError
    public void onError(Session session, Throwable throwable) {
        System.out.println("WebSocket error: " + throwable.getMessage());
    }

    @OnClose
    public void onClose(Session session, CloseReason reason) {
        System.out.println("WebSocket closed: " + reason);
    }
}
