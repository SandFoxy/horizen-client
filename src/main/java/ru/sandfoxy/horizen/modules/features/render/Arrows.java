package ru.sandfoxy.horizen.modules.features.render;

import imgui.ImDrawList;
import imgui.ImGui;
import imgui.ImVec4;
import imgui.flag.ImGuiCol;
import net.minecraft.client.MinecraftClient;
import net.minecraft.client.gui.screen.ingame.InventoryScreen;
import net.minecraft.entity.Entity;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.util.math.MathHelper;
import net.minecraft.util.math.Vec3d;
import ru.sandfoxy.horizen.imgui.utils.TextureManager;
import ru.sandfoxy.horizen.imgui.utils.ImGuiTexture;
import ru.sandfoxy.horizen.modules.core.Module;
import ru.sandfoxy.horizen.modules.core.type.Slider;
import ru.sandfoxy.horizen.utils.math.WorldToScreen;
import ru.sandfoxy.horizen.utils.render.RenderUtils;

import java.awt.*;

public class Arrows extends Module {
    private final MinecraftClient mc = MinecraftClient.getInstance();
    
    private final Slider sizeSlider = new Slider(60f, 30f, 120f, "Size", Slider.SliderType.FLOAT);
    private final Slider arrowSizeSlider = new Slider(35f, 10f, 50f, "Arrow Size", Slider.SliderType.FLOAT);

    public Arrows() {
        super("Arrows", CATEGORY.RENDER, "Arrows to players out of field of view.");
        this.addSetting(sizeSlider);
        this.addSetting(arrowSizeSlider);
    }

    @Override
    public void onDraw() {
        if (mc.player == null || mc.world == null) {
            return;
        }

        float screenWidth = ImGui.getIO().getDisplaySizeX();
        float screenHeight = ImGui.getIO().getDisplaySizeY();
        float centerX = screenWidth / 2f;
        float centerY = screenHeight / 2f;
        
        float arrowDistance = sizeSlider.getFloat();
        float arrowSize = arrowSizeSlider.getFloat();

        if (mc.currentScreen instanceof InventoryScreen) arrowDistance += 150;

        for (Entity entity : mc.world.getEntities()) {
            if (!(entity instanceof PlayerEntity player) || player == mc.player) {
                continue;
            }

            Vec3d playerPos = player.getPos();
            Vec3d screenPos = WorldToScreen.w2s(playerPos);

            if (screenPos != null && 
                screenPos.x >= 0 && screenPos.x <= screenWidth && 
                screenPos.y >= 0 && screenPos.y <= screenHeight) {
                continue;
            }

            Vec3d playerWorldPos = playerPos.subtract(mc.player.getPos());

            float yaw = mc.gameRenderer.getCamera().getYaw();

            double cos = MathHelper.cos((float) Math.toRadians(yaw));
            double sin = MathHelper.sin((float) Math.toRadians(yaw));
            
            double rotX = -playerWorldPos.x * cos - playerWorldPos.z * sin;
            double rotZ = playerWorldPos.x * sin - playerWorldPos.z * cos;

            float angle = (float) Math.toDegrees(Math.atan2(rotZ, rotX));

            double radians = Math.toRadians(angle);
            float arrowX = centerX + (float) (arrowDistance * Math.cos(radians));
            float arrowY = centerY + (float) (arrowDistance * Math.sin(radians));

            renderArrow(arrowX, arrowY, angle, arrowSize, player);
        }
    }

    private void renderArrow(float x, float y, float angle, float size, PlayerEntity player) {
        ImGuiTexture arrow = TextureManager.oofArrow;
        if (arrow == null) return;

        ImDrawList drawList = ImGui.getBackgroundDrawList();

        float radians = (float) Math.toRadians(angle + 90);
        float cos = (float) Math.cos(radians);
        float sin = (float) Math.sin(radians);

        ImVec4 buttonActiveColor = ImGui.getStyle().getColor(ImGuiCol.ButtonActive);

        float[] glowSizes = {size * 1.6f, size * 1.3f, size * 1.1f};
        float[] glowAlphas = {0.1f, 0.25f, 0.5f};
        
//        for (int i = 0; i < glowSizes.length; i++) {
//            renderArrowLayer(drawList, x, y, cos, sin, glowSizes[i], buttonActiveColor, glowAlphas[i], arrow);
//        }

        renderArrowLayer(drawList, x, y, cos, sin, size, buttonActiveColor, 0.65f, arrow);
    }
    
    private void renderArrowLayer(ImDrawList drawList, float x, float y, float cos, float sin, float size, ImVec4 baseColor, float alpha, ImGuiTexture arrow) {
        float halfWidth = size / 2f;
        float halfHeight = size / 2f;

        float[] dx = {-halfWidth, halfWidth, halfWidth, -halfWidth};
        float[] dy = {-halfHeight, -halfHeight, halfHeight, halfHeight};

        float[] rotatedX = new float[4];
        float[] rotatedY = new float[4];

        for (int i = 0; i < 4; i++) {
            rotatedX[i] = x + dx[i] * cos - dy[i] * sin;
            rotatedY[i] = y + dx[i] * sin + dy[i] * cos;
        }

        float uv1X = 0f, uv1Y = 0f;
        float uv2X = 1f, uv2Y = 0f;
        float uv3X = 1f, uv3Y = 1f;
        float uv4X = 0f, uv4Y = 1f;

        int color = RenderUtils.ConvertColor(new Color(baseColor.x, baseColor.y, baseColor.z, alpha));

        drawList.addImageQuad(
                arrow.textureId,
                rotatedX[0], rotatedY[0],
                rotatedX[1], rotatedY[1],
                rotatedX[2], rotatedY[2],
                rotatedX[3], rotatedY[3],
                uv1X, uv1Y,
                uv2X, uv2Y,
                uv3X, uv3Y,
                uv4X, uv4Y,
                color
        );
    }
}
