package ru.sandfoxy.horizen.imgui.notifications;

import java.util.Iterator;
import java.util.LinkedList;

import imgui.ImGui;

public class NotificationManager {
    private static final NotificationManager INSTANCE = new NotificationManager();
    private final LinkedList<Notification> notifications;
    private static final float PADDING = 15f;
    private static final float SPACING = 10f;

    private NotificationManager() {
        this.notifications = new LinkedList<>();
    }

    public static NotificationManager getInstance() {
        return INSTANCE;
    }

    public void addNotification(String title, String description, long duration) {
        notifications.add(new Notification(title, description, duration));
    }
    
    public void addNotification(String title, String description) {
        addNotification(title, description, 3000);
    }

    public void update() {
        Iterator<Notification> iterator = notifications.iterator();
        while (iterator.hasNext()) {
            Notification notification = iterator.next();
            notification.update();
            if (notification.shouldRemove()) {
                iterator.remove();
            }
        }
    }

    public void render() {
        if (notifications.isEmpty()) return;
        
        float screenWidth = ImGui.getIO().getDisplaySizeX();
        float screenHeight = ImGui.getIO().getDisplaySizeY();

        float currentY = screenHeight - PADDING;

        for (Iterator<Notification> it = notifications.descendingIterator(); it.hasNext(); ) {
            Notification notification = it.next();
            
            float notificationHeight = notification.getHeight();
            float notificationWidth = notification.getWidth();
            float yOffset = notification.getYOffset();

            float yPos = currentY - notificationHeight + yOffset;
            float xPos = screenWidth - notificationWidth - PADDING;

            ImGui.setNextWindowPos(xPos, yPos);

            notification.render();

            currentY -= notificationHeight + SPACING;
        }
    }
    
    public void removeNotification(int id) {
        notifications.removeIf(notification -> notification.getId() == id);
    }
    
    public void removeAllNotifications() {
        for (Notification notification : notifications) {
            notification.startRemoval();
        }
    }
    
    public int getNotificationCount() {
        return notifications.size();
    }

    public enum TYPE {
        INFO,
        ERROR,
        WARNING
    }
}