package ru.sandfoxy.horizen.modules.features.hack;

import imgui.ImDrawList;
import imgui.ImGui;
import net.minecraft.client.MinecraftClient;
import net.minecraft.util.math.Vec3d;
import net.minecraft.world.Heightmap;
import ru.sandfoxy.horizen.modules.core.Module;
import ru.sandfoxy.horizen.modules.core.type.SaveableList;
import ru.sandfoxy.horizen.utils.math.WorldToScreen;

import java.awt.*;
import java.util.ArrayList;
import java.util.List;
import java.util.Locale;

import static ru.sandfoxy.horizen.imgui.utils.TextureManager.locationMarker;
import static ru.sandfoxy.horizen.imgui.ImGuiUils.drawTextCentred;
import static ru.sandfoxy.horizen.utils.render.RenderUtils.ConvertColor;

public class GPS extends Module {
    private static final MinecraftClient mc = MinecraftClient.getInstance();
    
    public static SaveableList gpsPoints = new SaveableList("GPSPoints");
    
    public GPS() {
        super("GPS", CATEGORY.MISC, "GPS navigation system");
    }

    @Override
    public void onDraw(){
        if (mc.world == null || mc.player == null) return;

        List<String> points = gpsPoints.getList();
        for (String point : points) {
            try {
                GPSPoint gpsPoint = parseGPSPoint(point);
                if (gpsPoint != null) {
                    renderGPSPoint(gpsPoint);
                }
            } catch (Exception ignored) {}
        }
    }
    
    private void renderGPSPoint(GPSPoint point) {
        float actualY = point.y;
        if (point.y == -999999999) {
            actualY = mc.world.getTopY(Heightmap.Type.WORLD_SURFACE, (int) point.x, (int) point.z);
        }

        Vec3d targetLocation = new Vec3d(point.x, actualY, point.z);
        Vec3d targetOnScreen = WorldToScreen.w2s(targetLocation);
        if (targetOnScreen == null) return;

        ImDrawList drawList = ImGui.getBackgroundDrawList();

        Color markerColor = new Color(255, 255, 255);

        drawList.addImage(
                locationMarker.textureId,
                (float) (targetOnScreen.x - 14), (float) (targetOnScreen.y - 14),
                (float) (targetOnScreen.x + 14), (float) (targetOnScreen.y + 14),
                0, 0, 1, 1,
                ConvertColor(Color.black)
        );

        drawList.addImage(
                locationMarker.textureId,
                (float) (targetOnScreen.x - 12), (float) (targetOnScreen.y - 12),
                (float) (targetOnScreen.x + 12), (float) (targetOnScreen.y + 12),
                0, 0, 1, 1,
                ConvertColor(markerColor)
        );


        double distanceInBlocks = mc.player.getPos().distanceTo(targetLocation);
        String formattedDistance = formatDistance(distanceInBlocks);
        String displayText = String.format("%s [%s]", point.name, formattedDistance);

        drawTextCentred(displayText, (float) targetOnScreen.x, (float) targetOnScreen.y + 22, 14f, ConvertColor(markerColor), true);
    }
    
    private String formatDistance(double distance) {
        if (distance >= 1000) {
            double km = distance / 1000.0;
            if (km >= 10) {
                return String.format(Locale.US, "%.0fkm", km);
            } else {
                return String.format(Locale.US, "%.1fkm", km);
            }
        } else if (distance >= 1) {
            return String.format(Locale.US, "%.0fm", distance);
        } else {
            return String.format(Locale.US, "%.1fm", distance);
        }
    }
    
    public static void addGPSPoint(int x, int z, String name) {
        addGPSPoint(x, -999999999, z, name); // По умолчанию Y = 64
    }
    
    public static void addGPSPoint(int x, int y, int z, String name) {
        String pointString = String.format("%d,%d,%d,%s", x, y, z, name);
        gpsPoints.add(pointString);
    }
    
    public static void removeGPSPoint(String name) {
        List<String> points = new ArrayList<>(gpsPoints.getList());
        points.removeIf(point -> {
            GPSPoint gpsPoint = parseGPSPoint(point);
            return gpsPoint != null && gpsPoint.name.equalsIgnoreCase(name);
        });
        gpsPoints.getList().clear();
        gpsPoints.getList().addAll(points);
    }
    
    public static void removeGPSPointByIndex(int index) {
        List<String> points = gpsPoints.getList();
        if (index >= 0 && index < points.size()) {
            points.remove(index);
        }
    }
    
    public static List<GPSPoint> getAllGPSPoints() {
        List<GPSPoint> result = new ArrayList<>();
        for (String point : gpsPoints.getList()) {
            GPSPoint gpsPoint = parseGPSPoint(point);
            if (gpsPoint != null) {
                result.add(gpsPoint);
            }
        }
        return result;
    }
    
    public static void clearAllGPSPoints() {
        gpsPoints.getList().clear();
    }
    
    private static GPSPoint parseGPSPoint(String pointString) {
        try {
            String[] parts = pointString.split(",", 4);
            if (parts.length >= 3) {
                int x = Integer.parseInt(parts[0]);
                int y = Integer.parseInt(parts[1]);
                int z = Integer.parseInt(parts[2]);
                String name = parts.length == 4 ? parts[3] : "";
                return new GPSPoint(x, y, z, name);
            }
        } catch (NumberFormatException ignored) {}
        return null;
    }
    
    public static class GPSPoint {
        public final int x, y, z;
        public final String name;
        
        public GPSPoint(int x, int y, int z, String name) {
            this.x = x;
            this.y = y;
            this.z = z;
            this.name = name;
        }
        
        @Override
        public String toString() {
            if (name.isEmpty()) {
                return String.format("(%d, %d, %d)", x, y, z);
            } else {
                return String.format("%s (%d, %d, %d)", name, x, y, z);
            }
        }
    }
}
