package ru.sandfoxy.horizen.modules.features.render;

import com.mojang.blaze3d.systems.RenderSystem;
import imgui.ImDrawList;
import imgui.ImGui;
import net.minecraft.client.MinecraftClient;
import net.minecraft.entity.Entity;
import net.minecraft.entity.LivingEntity;
import net.minecraft.entity.decoration.ArmorStandEntity;
import net.minecraft.entity.mob.MobEntity;
import net.minecraft.entity.passive.AnimalEntity;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.text.Text;
import net.minecraft.util.math.Box;
import net.minecraft.util.math.Vec3d;
import org.lwjgl.opengl.GL11;
import ru.sandfoxy.horizen.modules.core.Module;
import ru.sandfoxy.horizen.modules.core.type.Checkbox;
import ru.sandfoxy.horizen.modules.core.type.ColorPicker;
import ru.sandfoxy.horizen.modules.core.type.ComboBox;
import ru.sandfoxy.horizen.modules.core.type.MultiComboBox;
import ru.sandfoxy.horizen.modules.features.hack.FriendList;
import ru.sandfoxy.horizen.utils.render.RenderUtils;
import ru.sandfoxy.horizen.utils.math.WorldToScreen;
import java.awt.*;
import java.util.List;
import java.util.Locale;
import java.util.Objects;

import static ru.sandfoxy.horizen.imgui.ImGuiUils.drawTextCentred;
import static ru.sandfoxy.horizen.imgui.ImGuiUils.drawTextCentredWithColors;
import static ru.sandfoxy.horizen.utils.render.RenderUtils.ConvertColor;
import static ru.sandfoxy.horizen.utils.render.RenderUtils.interpolate;

public class ESP extends Module {
    private final MinecraftClient mc = MinecraftClient.getInstance();
    public static final Checkbox isNameTags = new Checkbox(false, "NameTags");
    private final MultiComboBox targets = new MultiComboBox("Targets", List.of("Players"), List.of("Players", "Friends","Invisibles", "Mobs", "Animals"));
    private final Checkbox boxESP = new Checkbox(false, "Box ESP");
    private final ComboBox boxStyle = new ComboBox("Box Style", "3D", List.of("3D", "2D"));
    private final ComboBox boxMode = new ComboBox("Box Mode", "Static", List.of("Static", "Dynamic"));
    private final ColorPicker boxColor = new ColorPicker("Box Color", Color.BLUE);
    public static final Checkbox playerGlow = new Checkbox(true, "Glow");
    public static final ColorPicker glowColor = new ColorPicker("Glow Color", Color.CYAN);
    public ESP() {
        super("ESP", CATEGORY.RENDER, "Extrasensory Perception");

        this.addSetting(targets);
        this.addSetting(isNameTags);
        this.addSetting(boxESP);
        this.addSetting(boxStyle);
        this.addSetting(boxMode);
        this.addSetting(boxColor);
        this.addSetting(playerGlow);
        this.addSetting(glowColor);

        this.boxMode.visibleIf(boxESP::get);
        this.boxStyle.visibleIf(boxESP::get);
        this.boxColor.visibleIf(boxESP::get);
        this.glowColor.visibleIf(playerGlow::get);
    }

    private void render3DBox(Entity entity, int color) {
        Vec3d interpolatedPos = interpolate(entity, WorldToScreen.tickDelta);
        
        Box BoundingBox = entity.getBoundingBox();
        if (boxMode.isEquals("Static") && entity instanceof PlayerEntity){
            BoundingBox = new Box(
                interpolatedPos.x - 0.3, 
                interpolatedPos.y, 
                interpolatedPos.z - 0.3, 
                interpolatedPos.x + 0.3, 
                interpolatedPos.y + 1.8, 
                interpolatedPos.z + 0.3
            );
        } else {
            Vec3d delta = interpolatedPos.subtract(entity.getPos());
            BoundingBox = BoundingBox.offset(delta.x, delta.y, delta.z);
        }

        Vec3d min = BoundingBox.getMinPos().subtract(0,entity.getHeight() - 0.2f,0);
        Vec3d max = BoundingBox.getMaxPos().subtract(0,entity.getHeight() - 0.2f,0);

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

    public static void render2DBox(Entity entity, int color) {
        if (entity == null || MinecraftClient.getInstance().player == null) return;

        MinecraftClient mc = MinecraftClient.getInstance();
        Vec3d playerPos = mc.player.getPos();

        Vec3d interpolatedPos = interpolate(entity, WorldToScreen.tickDelta);

        Box box = entity.getBoundingBox();
        Vec3d entityPos = new Vec3d(
            interpolatedPos.x,
            interpolatedPos.y - entity.getHeight(),
            interpolatedPos.z
        );

        double dx = entityPos.x - playerPos.x;
        double dz = entityPos.z - playerPos.z;
        double angle = Math.atan2(dz, dx);

        double width = 0.75;
        double height = entity.getHeight() + 0.3f;

        double v1 = Math.cos(angle - Math.PI / 4) * width;
        double v2 = Math.sin(angle - Math.PI / 4) * width;
        double v3 = Math.cos(angle + Math.PI / 4) * width;
        double v4 = Math.sin(angle + Math.PI / 4) * width;
        Vec3d[] corners = new Vec3d[] {
            // Bottom corners
            new Vec3d(
                entityPos.x + v1,
                entityPos.y,
                entityPos.z + v2
            ),
            new Vec3d(
                entityPos.x + v3,
                entityPos.y,
                entityPos.z + v4
            ),
            // Top corners
            new Vec3d(
                entityPos.x + v1,
                entityPos.y + height,
                entityPos.z + v2
            ),
            new Vec3d(
                entityPos.x + v3,
                entityPos.y + height,
                entityPos.z + v4
            )
        };

        Vec3d[] screenCorners = new Vec3d[4];
        for (int i = 0; i < 4; i++) {
            screenCorners[i] = WorldToScreen.w2s(corners[i]);
            if (screenCorners[i] == null) return;
        }

        ImDrawList drawList = ImGui.getWindowDrawList();
        int outlineColor = 0xFF000000;

        // Draw outline first
        drawList.addLine(
            (float) screenCorners[0].x - 1, (float) screenCorners[0].y - 1,
            (float) screenCorners[1].x - 1, (float) screenCorners[1].y - 1,
            outlineColor
        );
        drawList.addLine(
            (float) screenCorners[1].x - 1, (float) screenCorners[1].y - 1,
            (float) screenCorners[3].x - 1, (float) screenCorners[3].y - 1,
            outlineColor
        );
        drawList.addLine(
            (float) screenCorners[3].x - 1, (float) screenCorners[3].y - 1,
            (float) screenCorners[2].x - 1, (float) screenCorners[2].y - 1,
            outlineColor
        );
        drawList.addLine(
            (float) screenCorners[2].x - 1, (float) screenCorners[2].y - 1,
            (float) screenCorners[0].x - 1, (float) screenCorners[0].y - 1,
            outlineColor
        );

        // Draw the main box
        drawList.addLine(
            (float) screenCorners[0].x, (float) screenCorners[0].y,
            (float) screenCorners[1].x, (float) screenCorners[1].y,
            color
        );
        drawList.addLine(
            (float) screenCorners[1].x, (float) screenCorners[1].y,
            (float) screenCorners[3].x, (float) screenCorners[3].y,
            color
        );
        drawList.addLine(
            (float) screenCorners[3].x, (float) screenCorners[3].y,
            (float) screenCorners[2].x, (float) screenCorners[2].y,
            color
        );
        drawList.addLine(
            (float) screenCorners[2].x, (float) screenCorners[2].y,
            (float) screenCorners[0].x, (float) screenCorners[0].y,
            color
        );
    }

    private void drawLine(ImDrawList drawList, Vec3d from, Vec3d to, int color) {
        drawList.addLine((float) from.x, (float) from.y, (float) to.x, (float) to.y, color);
    }

    private void NameTags(Entity ent) {
        if (ent == null || mc.player == null || !(ent instanceof LivingEntity livingEntity))
            return;

        Vec3d nametag = WorldToScreen.w2s(interpolate(ent, WorldToScreen.tickDelta).add(0,0.5f,0));

        if (nametag == null) return;

        float distance = mc.player.distanceTo(ent);
        float scale = Math.max(0.5f, 2.0f - distance / 8.0f);


        //RenderUtils.drawPlayerInfo(prefix + nickname, nametag, RenderUtils.ConvertColor(Color.decode(prefixColor)));;
        String prefix = "";
        String prefixColor = "#FFFFFF";

        boolean hasPrefix = false;
        boolean isFriend = false;

        Color textColor = Color.white;
        if (ent instanceof PlayerEntity playerEntity){
            try {
                Text sibilngs = ent.getDisplayName().getSiblings().get(0);
                if (sibilngs != null){
                    Text subSibling = sibilngs.getSiblings().get(1);
                    prefix = subSibling.getString();
                    prefixColor = subSibling.getStyle().getColor().getHexCode();
                    hasPrefix = true;
                }
            } catch (Exception ignored) {}

            isFriend = FriendList.friendList.getList().contains(ent.getName().getString());
        }

        StringBuilder completeText = new StringBuilder();
        if (isFriend) completeText.append("#00FF00 [F]");
        if (hasPrefix) completeText.append(prefixColor).append(" ").append(prefix);
        completeText.append("#FFFFFF").append(ent instanceof PlayerEntity playerEntity ? playerEntity.getGameProfile().getName() : ent.getName().getString());

        double health = livingEntity.getHealth();
        double maxHealth = livingEntity.getMaxHealth();

        completeText.append("#b5b5b5[#FF0000")
                .append((health % 1 == 0) ? (int) health : String.format(Locale.US, "%.1f", health))
                .append("#b5b5b5]");


        drawTextCentredWithColors(completeText.toString(), (float) nametag.x, (float) nametag.y, 12.f,1, true);
    }

    private void renderHealthBar(Entity entity, Vec3d screenPos) {
        //TODO: Healthbar
    }


    @Override
    public void onDraw() {
        if (mc.world == null || mc.player == null)
            return;

        List<String> targetSet = targets.getList();
        for (Entity entity : mc.world.getEntities()) {
            if (!(entity instanceof LivingEntity livingEntity) || entity == mc.player) continue;
            if (livingEntity.getHealth() <= 0) continue;

            if (entity instanceof ArmorStandEntity) continue;
            if (entity instanceof AnimalEntity) {
                if (!targetSet.contains("Animals")) continue;
            } else if (entity instanceof MobEntity && !targetSet.contains("Mobs")) continue;
            if (entity instanceof PlayerEntity && !targetSet.contains("Players")) continue;
            if (FriendList.friendList.getList().contains(entity.getName().getString()) && !targetSet.contains("Friends")) continue;
            if (entity.isInvisible() && !targetSet.contains("Invisibles")) continue;

            Vec3d entPos = RenderUtils.interpolate(entity, WorldToScreen.tickDelta);
            Vec3d screen = WorldToScreen.w2s(entPos);

            if (screen == null) continue;

            if (boxESP.getValue()){
                switch (boxStyle.getValue()){
                    case "3D": render3DBox(entity, FriendList.friendList.getList().contains(entity.getName().getString()) ? ConvertColor(Color.GREEN) : boxColor.getIntColor()); break;
                    case "2D": render2DBox(entity, FriendList.friendList.getList().contains(entity.getName().getString()) ? ConvertColor(Color.GREEN) : boxColor.getIntColor()); break;
                }
            }

            if (isNameTags.getValue())
                NameTags(entity);
        }
    }
}