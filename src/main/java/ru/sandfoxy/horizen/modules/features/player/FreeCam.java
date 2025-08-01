package ru.sandfoxy.horizen.modules.features.player;

import net.minecraft.client.MinecraftClient;
import net.minecraft.client.network.OtherClientPlayerEntity;
import net.minecraft.entity.Entity;
import net.minecraft.util.math.MathHelper;
import net.minecraft.util.math.Vec3d;
import ru.sandfoxy.horizen.modules.core.Module;
import ru.sandfoxy.horizen.modules.core.type.Slider;
import ru.sandfoxy.horizen.utils.math.WorldToScreen;

public class FreeCam extends Module {
    private static final MinecraftClient mc = MinecraftClient.getInstance();

    private OtherClientPlayerEntity fakePlayer;
    private Vec3d originalPos;
    private float originalYaw, originalPitch;
    private Vec3d cameraPos;
    private final Slider speedSlider = new Slider(1.0f, 0.1f, 5.0f, "Speed", Slider.SliderType.FLOAT);

    public FreeCam() {
        super("FreeCam", CATEGORY.PLAYER, "Fly freely with noclip and smooth motion");
        this.addSetting(speedSlider);
    }

    @Override
    public void onEnable() {
        if (mc.player == null || mc.world == null) return;

        originalPos = mc.player.getPos();
        originalYaw = mc.player.getYaw();
        originalPitch = mc.player.getPitch();

        fakePlayer = new OtherClientPlayerEntity(mc.world, mc.player.getGameProfile());
        fakePlayer.copyPositionAndRotation(mc.player);
        fakePlayer.setHeadYaw(mc.player.getHeadYaw());
        fakePlayer.setBodyYaw(mc.player.getBodyYaw());
        fakePlayer.setSneaking(mc.player.isSneaking());
        fakePlayer.setPose(mc.player.getPose());
        fakePlayer.setId(-12345);
        mc.world.addEntity(fakePlayer);

        mc.setCameraEntity(mc.player);
        mc.player.noClip = true;
        cameraPos = mc.player.getPos();
    }

    @Override
    public void onDisable() {
        if (mc.player == null || mc.world == null) return;

        mc.player.noClip = false;
        mc.player.setPosition(originalPos);
        mc.player.setYaw(originalYaw);
        mc.player.setPitch(originalPitch);

        mc.setCameraEntity(mc.player);

        if (fakePlayer != null) {
            mc.world.removeEntity(fakePlayer.getId(), Entity.RemovalReason.DISCARDED);
            fakePlayer = null;
        }
    }

    @Override
    public void startTick() {
        if (mc.player == null || mc.world == null || fakePlayer == null) return;

        mc.player.noClip = true;
        mc.player.setVelocity(Vec3d.ZERO);
        mc.player.setOnGround(false);
        mc.player.fallDistance = 0;

        float speed = speedSlider.getFloat();
        float yaw = mc.player.getYaw();
        float pitch = mc.player.getPitch();

        float yawRad = (float) Math.toRadians(yaw);
        float pitchRad = (float) Math.toRadians(pitch);

        Vec3d forward = new Vec3d(
                -MathHelper.sin(yawRad) * MathHelper.cos(pitchRad),
                -MathHelper.sin(pitchRad),
                MathHelper.cos(yawRad) * MathHelper.cos(pitchRad)
        ).normalize().multiply(speed);

        Vec3d strafe = new Vec3d(
                MathHelper.cos(yawRad),
                0,
                MathHelper.sin(yawRad)
        ).normalize().multiply(speed);

        Vec3d motion = Vec3d.ZERO;

        if (mc.options.forwardKey.isPressed()) motion = motion.add(forward);
        if (mc.options.backKey.isPressed()) motion = motion.subtract(forward);
        if (mc.options.leftKey.isPressed()) motion = motion.add(strafe);
        if (mc.options.rightKey.isPressed()) motion = motion.subtract(strafe);
        if (mc.options.jumpKey.isPressed()) motion = motion.add(0, speed, 0);
        if (mc.options.sneakKey.isPressed()) motion = motion.subtract(0, speed, 0);

        cameraPos = cameraPos.lerp(cameraPos.add(motion), WorldToScreen.tickDelta);
        mc.player.setPosition(cameraPos);

        fakePlayer.setPosition(originalPos);
        fakePlayer.setYaw(originalYaw);
        fakePlayer.setPitch(originalPitch);
        fakePlayer.setHeadYaw(originalYaw);
        fakePlayer.setBodyYaw(originalYaw);
    }
}
