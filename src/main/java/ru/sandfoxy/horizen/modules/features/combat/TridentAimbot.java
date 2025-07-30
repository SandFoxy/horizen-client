package ru.sandfoxy.horizen.modules.features.combat;

import imgui.ImGui;
import imgui.ImVec4;
import imgui.flag.ImGuiCol;
import net.minecraft.client.MinecraftClient;
import net.minecraft.entity.Entity;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.item.Items;
import net.minecraft.sound.SoundEvents;
import net.minecraft.util.math.MathHelper;
import net.minecraft.util.math.Vec3d;
import ru.sandfoxy.horizen.imgui.utils.ImGuiTexture;
import ru.sandfoxy.horizen.imgui.utils.TextureManager;
import ru.sandfoxy.horizen.modules.core.Module;
import ru.sandfoxy.horizen.utils.render.RenderUtils;
import ru.sandfoxy.horizen.utils.math.RotationUtils;
import ru.sandfoxy.horizen.utils.math.WorldToScreen;

import java.awt.*;

import static ru.sandfoxy.horizen.utils.render.RenderUtils.ConvertColor;
import static ru.sandfoxy.horizen.utils.math.RotationUtils.Rotation;

public class TridentAimbot extends Module {
    private static MinecraftClient mc = MinecraftClient.getInstance();
    private static Entity targetEntity;
    public TridentAimbot() {
        super("TridentAimbot", CATEGORY.COMBAT, "Will aim you at your target while you are using trident.", "Aimbot\0ProjectileHelper\0Helper");
    }

    private float scanAnim = 0.f;
    private boolean scanAnimRaise = true;

    private static float realYaw = 0.f;
    private static float realPitch = 0.f;

    @Override
    public void startTick() {
        if (mc.world == null || mc.player == null) return;

        if (mc.player.getMainHandStack().getItem() != Items.TRIDENT || !mc.options.useKey.isPressed()) {
            realYaw = 0;
            realPitch = 0;
            targetEntity = null;
            return;
        }

        targetEntity = null;
        float closest_distance = Float.MAX_VALUE;

        for (Entity entity : mc.world.getEntities()) {
            if (!(entity instanceof PlayerEntity player)) continue;
            if (entity == mc.player || !entity.isAlive()) continue;
            if (!mc.player.canSee(entity)) continue;

            float distance = mc.player.distanceTo(entity);
            if (distance < closest_distance) {
                closest_distance = distance;
                targetEntity = entity;
            }
        }

        if (targetEntity == null) {
            realYaw   = 0f;
            realPitch = 0f;
            return;
        };

        realYaw = MathHelper.wrapDegrees(mc.player.getYaw());
        realPitch = MathHelper.wrapDegrees(mc.player.getPitch());

        Rotation neededRotation = calculateTridentRotation(targetEntity.getEyePos().add(targetEntity.getVelocity()));

        float yawDelta = MathHelper.wrapDegrees(neededRotation.yaw - realYaw);
        float pitchDelta = MathHelper.wrapDegrees(neededRotation.pitch - realPitch);
        mc.player.changeLookDirection(yawDelta * 10f, pitchDelta * 10f);


        mc.player.playSound(SoundEvents.BLOCK_AMETHYST_BLOCK_STEP, 1.f, 1.f);
    }


    @Override
    public void onDraw(){
        if (targetEntity == null){ return; }
        if (scanAnimRaise) { scanAnim += WorldToScreen.tickDelta / 100; }
        else { scanAnim -= WorldToScreen.tickDelta / 100; }

        if (scanAnim >= targetEntity.getBoundingBox().getLengthY()){
            scanAnimRaise = false;
        }
        if (scanAnim <= 0f){
            scanAnimRaise = true;
        }

        Vec3d AnimationPos = targetEntity.getPos().add(0,scanAnim,0);
        Vec3d playerCenter = WorldToScreen.w2s(RenderUtils.interpolate(targetEntity, WorldToScreen.tickDelta).add(0, -0.5f, 0));

        if (playerCenter != null) {
            ImGuiTexture targethud = TextureManager.targethud;

            float width = targethud.width / 10;
            float height = targethud.height / 10;
            float centerX = (float) playerCenter.x;
            float centerY = (float) playerCenter.y;

            float rotation = scanAnim * 100f;
            float radians = (float) Math.toRadians(rotation);

            float cos = (float) Math.cos(radians);
            float sin = (float) Math.sin(radians);

            float[] dx = {-width / 2, width / 2, width / 2, -width / 2};
            float[] dy = {-height / 2, -height / 2, height / 2, height / 2};

            float[] x = new float[4];
            float[] y = new float[4];

            for (int i = 0; i < 4; i++) {
                x[i] = centerX + dx[i] * cos - dy[i] * sin;
                y[i] = centerY + dx[i] * sin + dy[i] * cos;
            }

            float uv1X = 0f, uv1Y = 0f;
            float uv2X = 1f, uv2Y = 0f;
            float uv3X = 1f, uv3Y = 1f;
            float uv4X = 0f, uv4Y = 1f;

            ImVec4 hudColor = ImGui.getStyle().getColor(ImGuiCol.ButtonActive);
            ImGui.getBackgroundDrawList().addImageQuad(
                    targethud.textureId,
                    x[0], y[0],
                    x[1], y[1],
                    x[2], y[2],
                    x[3], y[3],
                    uv1X, uv1Y,
                    uv2X, uv2Y,
                    uv3X, uv3Y,
                    uv4X, uv4Y,
                    ConvertColor(new Color(hudColor.x, hudColor.y,hudColor.z, hudColor.w))
            );
        }
    }
    private RotationUtils.Rotation calculateTridentRotation(Vec3d targetPos) {
        Vec3d playerPos = mc.player.getEyePos();
        Vec3d delta = targetPos.subtract(playerPos);

        // Учитываем гравитацию и скорость трезубца
        double gravity = 0.05; // Гравитация трезубца
        double velocity = 2.5; // Скорость броска трезубца (примерное значение)

        double distance = delta.horizontalLength();
        double height = delta.y;

        // Рассчитываем угол наклона (pitch) с учетом баллистики
        double pitch = -Math.toDegrees(Math.atan((velocity * velocity - Math.sqrt(velocity * velocity * velocity * velocity - gravity * (gravity * distance * distance + 2 * height * velocity * velocity))) / (gravity * distance)));

        // Рассчитываем угол поворота (yaw)
        double yaw = Math.toDegrees(Math.atan2(delta.z, delta.x)) - 90;

        return new RotationUtils.Rotation((float) yaw, (float) pitch);
    }
    @Override
    public void onDisable(){
        targetEntity = null;
        realPitch = 0.f;
        realYaw = 0.f;
    }
}
