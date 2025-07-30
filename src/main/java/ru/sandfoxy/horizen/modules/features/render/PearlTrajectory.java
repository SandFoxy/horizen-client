package ru.sandfoxy.horizen.modules.features.render;

import imgui.ImDrawList;
import imgui.ImGui;
import net.minecraft.client.MinecraftClient;
import net.minecraft.entity.Entity;
import net.minecraft.entity.projectile.thrown.EnderPearlEntity;
import net.minecraft.util.hit.HitResult;
import net.minecraft.world.RaycastContext;
import net.minecraft.util.math.Vec3d;
import ru.sandfoxy.horizen.modules.core.Module;
import ru.sandfoxy.horizen.modules.core.type.ColorPicker;
import ru.sandfoxy.horizen.modules.core.type.Slider;
import ru.sandfoxy.horizen.utils.math.WorldToScreen;

import java.awt.Color;
import java.util.*;

import static ru.sandfoxy.horizen.utils.render.RenderUtils.ConvertColor;

public class PearlTrajectory extends Module {
    private static final MinecraftClient mc = MinecraftClient.getInstance();
    private final ColorPicker trajectoryColor = new ColorPicker("Trajectory Color", new Color(255, 0, 255, 180));
    private final Slider pointsCount = new Slider(20, 10, 50, "Points Count", Slider.SliderType.INT);
    
    private static class PearlData {
        List<Vec3d> trajectoryPoints = new ArrayList<>();
        double landingTime = 0;
    }
    
    private final Map<Integer, PearlData> pearlDataMap = new HashMap<>();

    public PearlTrajectory() {
        super("PearlTrajectory", CATEGORY.RENDER, "Shows the trajectory of thrown ender pearls");
        this.addSetting(trajectoryColor);
        this.addSetting(pointsCount);
    }

    @Override
    public void onDraw() {
        if (mc.world == null || mc.player == null) return;

        ImDrawList drawList = ImGui.getBackgroundDrawList();
        
        // Clear old pearl data
        pearlDataMap.clear();

        for (Entity entity : mc.world.getEntities()) {
            if (!(entity instanceof EnderPearlEntity)) continue;

            PearlData data = new PearlData();
            calculateTrajectory((EnderPearlEntity) entity, data);
            pearlDataMap.put(entity.getId(), data);
            drawTrajectory(drawList, data);
        }
    }

    private void calculateTrajectory(EnderPearlEntity pearl, PearlData data) {
        Vec3d pos = pearl.getPos();
        Vec3d vel = pearl.getVelocity();

        // Minecraft pearl physics constants
        double gravity = 0.03;
        double drag = 0.99;
        double dt = 0.05; 
        int maxSteps = 5000;
        int step = 0;

        Vec3d currentPos = pos;
        Vec3d currentVel = vel;
        data.trajectoryPoints.add(currentPos);

        while (step < maxSteps) {
            // Apply gravity
            currentVel = new Vec3d(
                currentVel.x * drag,
                currentVel.y * drag - gravity,
                currentVel.z * drag
            );

            // Calculate next position
            Vec3d nextPos = new Vec3d(
                currentPos.x + currentVel.x,
                currentPos.y + currentVel.y,
                currentPos.z + currentVel.z
            );

            RaycastContext context = new RaycastContext(currentPos, nextPos, RaycastContext.ShapeType.COLLIDER, RaycastContext.FluidHandling.NONE, pearl);
            HitResult result = mc.world.raycast(context);

            if (result.getType() != HitResult.Type.MISS) {
                Vec3d hitPos = result.getPos();
                data.trajectoryPoints.add(hitPos);
                data.landingTime = step * dt;
                break;
            } else {
                data.trajectoryPoints.add(nextPos);
                currentPos = nextPos;
                step++;
            }
        }
    }

    private void drawTrajectory(ImDrawList drawList, PearlData data) {
        if (data.trajectoryPoints.size() < 2) return;

        int color = ConvertColor(trajectoryColor.getColor());
        Vec3d prevScreen = null;

        for (int i = 0; i < data.trajectoryPoints.size(); i++) { 
            Vec3d point = data.trajectoryPoints.get(i);
            Vec3d screen = WorldToScreen.w2s(point);
            if (screen == null) continue;

            if (prevScreen != null) {
                drawList.addLine(
                        (float) prevScreen.x, (float) prevScreen.y,
                        (float) screen.x, (float) screen.y,
                        color,
                        2.0f
                );
            }

            // Draw landing time text at the last point
            if (i == data.trajectoryPoints.size() - 1) {
                String timeText = String.format("%.2fs", data.landingTime);
                drawList.addText(
                        (float) screen.x + 5,
                        (float) screen.y,
                        color,
                        timeText
                );
            }

            prevScreen = screen;
        }
    }
}