package ru.sandfoxy.horizen.utils;

import javax.websocket.*;
import java.net.URI;
import java.util.concurrent.Executors;
import java.util.concurrent.ScheduledExecutorService;
import java.util.concurrent.TimeUnit;

@ClientEndpoint
public class Tracker {
    private static Session session;
    private static String serverUri;
    private static boolean isReconnecting = false;
    private static int reconnectAttempts = 0;
    private static final int MAX_RECONNECT_ATTEMPTS = 10;
    private static final long INITIAL_RECONNECT_DELAY = 1000;
    private static final ScheduledExecutorService scheduler = Executors.newScheduledThreadPool(1);

    public static void connect(String uri) {
        serverUri = uri;
        connectInternal(uri);
    }

    private static void connectInternal(String uri) {
        try {
            WebSocketContainer container = ContainerProvider.getWebSocketContainer();
            session = container.connectToServer(Tracker.class, new URI(uri));
            reconnectAttempts = 0;
            isReconnecting = false;
            System.out.println("Connected to WebSocket server");
        } catch (Exception e) {
            System.out.println("Connection failed: " + e.getMessage());
            if (!isReconnecting && serverUri != null) {
                scheduleReconnect();
            }
        }
    }

    private static void scheduleReconnect() {
        if (reconnectAttempts >= MAX_RECONNECT_ATTEMPTS) {
            System.out.println("Max reconnection attempts reached");
            return;
        }

        isReconnecting = true;
        reconnectAttempts++;
        
        long delay = INITIAL_RECONNECT_DELAY * (long) Math.pow(2, reconnectAttempts - 1);
        delay = Math.min(delay, 30000);
        
        System.out.println("Scheduling reconnect attempt " + reconnectAttempts + " in " + delay + "ms");
        
        scheduler.schedule(() -> {
            System.out.println("Attempting to reconnect...");
            connectInternal(serverUri);
        }, delay, TimeUnit.MILLISECONDS);
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

    public static void disconnect() {
        try {
            if (session != null && session.isOpen()) {
                session.close();
            }
            isReconnecting = false;
            reconnectAttempts = 0;
        } catch (Exception e) {
            System.out.println("Disconnect failed: " + e.getMessage());
        }
    }

    public static boolean isConnected() {
        return session != null && session.isOpen();
    }

    @OnError
    public void onError(Session session, Throwable throwable) {
        System.out.println("WebSocket error: " + throwable.getMessage());
    }

    @OnClose
    public void onClose(Session session, CloseReason reason) {
        System.out.println("WebSocket closed: " + reason);
        if (!isReconnecting && serverUri != null) {
            scheduleReconnect();
        }
    }

    @OnOpen
    public void onOpen(Session session) {
        System.out.println("WebSocket connection opened");
    }
}
