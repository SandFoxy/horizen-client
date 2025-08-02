package ru.sandfoxy.horizen.utils.render;

import imgui.ImDrawList;
import imgui.ImGui;
import imgui.ImVec2;
import imgui.flag.ImDrawFlags;
import net.minecraft.client.MinecraftClient;
import net.minecraft.client.font.TextRenderer;
import net.minecraft.client.gui.DrawContext;
import net.minecraft.client.render.RenderLayer;
import net.minecraft.entity.Entity;
import net.minecraft.entity.effect.StatusEffects;
import net.minecraft.entity.player.HungerManager;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.util.Identifier;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.MathHelper;
import net.minecraft.util.math.Vec3d;
import net.minecraft.util.math.random.Random;
import ru.sandfoxy.horizen.utils.math.WorldToScreen;

import java.awt.*;
import java.util.ArrayList;
import java.util.List;

public class RenderUtils {
    public static void Draw3DRing(Vec3d position, float radius, int points, int color, int fillColor) {
        if (points <= 2) return;

        float step = (float) ((Math.PI * 2.0) / points);
        List<ImVec2> screenPoints = new ArrayList<>();

        for (float angle = 0f; angle <= Math.PI * 2.0; angle += step) {
            double dx = Math.cos(angle) * radius;
            double dz = Math.sin(angle) * radius;
            Vec3d worldPos = new Vec3d(position.x + dx, position.y, position.z + dz);

            Vec3d screenPos = WorldToScreen.w2s(worldPos);
            if (screenPos != null){
                screenPoints.add(new ImVec2((float) screenPos.x, (float) screenPos.y));
            }

        }

        if (screenPoints.size() > 2) {
            ImVec2[] pointArray = screenPoints.toArray(new ImVec2[0]);

            if (fillColor != -1) ImGui.getBackgroundDrawList().addConvexPolyFilled(pointArray, pointArray.length, fillColor);
            ImGui.getBackgroundDrawList().addPolyline(pointArray, pointArray.length, color, ImDrawFlags.Closed, 1.5f);
        }
    }

    public static int ConvertColor(Color color) {
        int r = color.getRed();
        int g = color.getGreen();
        int b = color.getBlue();
        int a = color.getAlpha();

        return (a << 24) | (b << 16) | (g << 8) | r;
    }


    private static void drawLine(ImDrawList drawList, Vec3d from, Vec3d to, int color) {
        drawList.addLine((float) from.x, (float) from.y, (float) to.x, (float) to.y, color);
    }

    public static void drawPlayerInfo(String label, Vec3d position, int indicatorColor, int bgColor) {
        ImDrawList drawList = ImGui.getForegroundDrawList();

        float[] textSize = new float[]{ImGui.calcTextSize(label).x,ImGui.calcTextSize(label).y};
        Vec3d labelSize = new Vec3d(textSize[0], textSize[1], 0);

        Vec3d padding = new Vec3d(10.0f, 5.0f, 0);
        Vec3d boxSize = labelSize.add(new Vec3d(20.0f, 10.0f, 0));
        Vec3d indicatorSize = new Vec3d(5.0f, boxSize.y, 0);

        Vec3d totalSize = new Vec3d(indicatorSize.x + boxSize.x, boxSize.y, 0);
        Vec3d centeredPos = position.subtract(totalSize.multiply(0.5));

        Vec3d indicatorPos = centeredPos;
        Vec3d bgPos = centeredPos.add(indicatorSize);
        Vec3d bgSize = new Vec3d(boxSize.x, boxSize.y, 0);

        // Draw indicator
        drawList.addRectFilled(
                (float) ((float) indicatorPos.x), (float) ((float) indicatorPos.y + indicatorSize.y),
                (float) (indicatorPos.x + indicatorSize.x), (float) (indicatorPos.y + indicatorSize.y + indicatorSize.y),
                indicatorColor
        );

        // Draw background
        drawList.addRectFilled(
                (float) bgPos.x, (float) bgPos.y,
                (float) (bgPos.x + bgSize.x), (float) (bgPos.y + bgSize.y),
                bgColor
        );

        // Center text in box
        Vec3d textPos = bgPos.add(
                new Vec3d(
                        (bgSize.x - labelSize.x) / 2.0,
                        (bgSize.y - labelSize.y) / 2.0,
                        0
                )
        );

        drawList.addText(
                (float) textPos.x, (float) textPos.y,
                0xFFFFFFFF, // white color in ARGB
                label
        );
    }

    public static void drawPlayerInfo(String label, Vec3d position, int indicatorColor) {
        drawPlayerInfo(label, position, indicatorColor,ImGui.getColorU32(15f / 255f, 15f / 255f, 15f / 255f, 0.8f));
    }

    public static void drawBlock(BlockPos block, int color) {
        if (block == null) return;

        Vec3d min = new Vec3d(block.getX(), block.getY(), block.getZ());
        Vec3d max = new Vec3d(block.getX() + 1, block.getY() + 1, block.getZ() + 1);

        Vec3d[] corners = new Vec3d[] {
                new Vec3d(min.x, min.y, min.z),
                new Vec3d(max.x, min.y, min.z),
                new Vec3d(max.x, min.y, max.z),
                new Vec3d(min.x, min.y, max.z),
                new Vec3d(min.x, max.y, min.z),
                new Vec3d(max.x, max.y, min.z),
                new Vec3d(max.x, max.y, max.z),
                new Vec3d(min.x, max.y, max.z)
        };

        Vec3d[] projected = new Vec3d[8];
        for (int i = 0; i < 8; i++) {
            projected[i] = WorldToScreen.w2s(corners[i]);
            if (projected[i] == null) return;
        }

        ImDrawList drawList = ImGui.getWindowDrawList();

        // Bottom rectangle
        Color skibidiColor = new Color(color);
        int fillColor = ImGui.getColorU32(skibidiColor.getRed() / 255f, skibidiColor.getGreen() / 255f, skibidiColor.getBlue() / 255f, 0.5f);

        drawLine(drawList, projected[0], projected[1], color);
        drawLine(drawList, projected[1], projected[2], color);
        drawLine(drawList, projected[2], projected[3], color);
        drawLine(drawList, projected[3], projected[0], color);

        // Top rectangle
        drawLine(drawList, projected[4], projected[5], color);
        drawLine(drawList, projected[5], projected[6], color);
        drawLine(drawList, projected[6], projected[7], color);
        drawLine(drawList, projected[7], projected[4], color);

        // Vertical lines
        drawLine(drawList, projected[0], projected[4], color);
        drawLine(drawList, projected[1], projected[5], color);
        drawLine(drawList, projected[2], projected[6], color);
        drawLine(drawList, projected[3], projected[7], color);
    }
    public static  Vec3d interpolate(Entity entity, float tickDelta) {
        double x = MathHelper.lerp(tickDelta, entity.lastRenderX, entity.getX());
        double y = MathHelper.lerp(tickDelta, entity.lastRenderY, entity.getY());
        double z = MathHelper.lerp(tickDelta, entity.lastRenderZ, entity.getZ());
        return new Vec3d(x, y + entity.getStandingEyeHeight(), z);
    }

    static public void renderFood(
            Identifier FOOD_EMPTY_HUNGER_TEXTURE,
            Identifier FOOD_HALF_HUNGER_TEXTURE,
            Identifier FOOD_FULL_HUNGER_TEXTURE,
            Identifier FOOD_EMPTY_TEXTURE,
            Identifier FOOD_HALF_TEXTURE,
            Identifier FOOD_FULL_TEXTURE,
            Random random,
            int ticks,
            DrawContext context,
            PlayerEntity player,
            int top, int right
    ) {
        HungerManager hungerManager = player.getHungerManager();
        int foodLevel = hungerManager.getFoodLevel() + (int) hungerManager.getSaturationLevel();

        for (int j = 0; j < 10 + (hungerManager.getSaturationLevel() / 2); ++j) {
            int x = right - (j % 10) * 8 - 9;
            int y = top - (j / 10) * 10;

            Identifier emptyTexture;
            Identifier halfTexture;
            Identifier fullTexture;

            if (player.hasStatusEffect(StatusEffects.HUNGER)) {
                emptyTexture = FOOD_EMPTY_HUNGER_TEXTURE;
                halfTexture = FOOD_HALF_HUNGER_TEXTURE;
                fullTexture = FOOD_FULL_HUNGER_TEXTURE;
            } else {
                emptyTexture = FOOD_EMPTY_TEXTURE;
                halfTexture = FOOD_HALF_TEXTURE;
                fullTexture = FOOD_FULL_TEXTURE;
            }

            if (hungerManager.getSaturationLevel() <= 0.0F && ticks % (foodLevel * 3 + 1) == 0) {
                y += random.nextInt(3) - 1;
            }

            context.drawGuiTexture(RenderLayer::getGuiTextured, emptyTexture, x, y, 9, 9);

            if (j * 2 + 1 < foodLevel) {
                context.drawGuiTexture(RenderLayer::getGuiTextured, fullTexture, x, y, 9, 9);
            } else if (j * 2 + 1 == foodLevel) {
                context.drawGuiTexture(RenderLayer::getGuiTextured, halfTexture, x, y, 9, 9);
            }
        }
    }

    public static void renderChangelog(DrawContext context,int x, int y, String text, LogType type){
        Color color = switch (type) {
            case LogType.ADD -> Color.green;
            case LogType.REMOVED -> Color.red;
            case LogType.FIXED -> Color.CYAN;
            case LogType.IMPROVED -> Color.orange;
        };

        String prefix= switch (type) {
            case LogType.ADD -> "+";
            case LogType.REMOVED -> "-";
            case LogType.FIXED -> "/";
            case LogType.IMPROVED -> "*";
        };

        TextRenderer textRenderer = MinecraftClient.getInstance().textRenderer;

        context.drawText(textRenderer,"[" + prefix + "]", x,y, ConvertColor(color), true);
        x += (int) (textRenderer.getWidth(prefix) + 8f);
        context.drawText(textRenderer,text, x,y, ConvertColor(Color.white), true);
    }

    public static void titlescreenRender(DrawContext context, float tickDelta){
        int startY = 5;
        int lineHeight = 18;
        
        renderChangelog(context, 5, startY + lineHeight, "Added Chest Stealler", LogType.ADD);
        renderChangelog(context, 5, startY + lineHeight * 2, "Added GPS Menu", LogType.ADD);
        renderChangelog(context, 5, startY + lineHeight * 3, "Hitbox size is now more precise", LogType.IMPROVED);
        renderChangelog(context, 5, startY + lineHeight * 4, "ImGui Demo removed", LogType.REMOVED);
    }

    private enum LogType{
        ADD,
        IMPROVED,
        FIXED,
        REMOVED
    }
}
