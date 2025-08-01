package ru.sandfoxy.horizen.modules;

import com.google.common.collect.Maps;
import net.minecraft.client.MinecraftClient;
import net.minecraft.client.gui.hud.BossBarHud;
import net.minecraft.client.gui.hud.ClientBossBar;
import ru.sandfoxy.horizen.imgui.notifications.NotificationManager;
import ru.sandfoxy.horizen.utils.Tracker;

import java.util.Map;
import java.util.UUID;

public class ServerInfo {
    private static final MinecraftClient mc = MinecraftClient.getInstance();
    public static String serverIP;
    private static String lastNotifiedIP;
    public static Map<UUID, ClientBossBar> bossBars = Maps.newLinkedHashMap();

    public static void update() {
        if (mc.getCurrentServerEntry() != null) {
            serverIP = mc.getCurrentServerEntry().address;
            if (mc.player != null && mc.player.getDisplayName() != null){
                Tracker.send(mc.player.getDisplayName().getString(), serverIP);
            }else {
                Tracker.send("Unknown", serverIP);
            }
            if (serverIP.endsWith(".funtime.su") && !serverIP.equals(lastNotifiedIP)) {
                NotificationManager.getInstance().addNotification("Server Info", "Connected to FunTime!", 3000);
                lastNotifiedIP = serverIP;
            }
        } else {
            serverIP = null;
            lastNotifiedIP = null;
        }
    }

    public static boolean isInPvP() {
        if (mc.inGameHud == null) return false;
        
        BossBarHud bossBarHud = mc.inGameHud.getBossBarHud();
        if (bossBarHud == null) return false;

        for (ClientBossBar bossBar : ServerInfo.bossBars.values()) {
            if (bossBar.getName().getString().toLowerCase().contains("pvp")) {
                return true;
            }
        }
        return false;
    }
}
