package ru.sandfoxy.horizen.utils.math;

import net.minecraft.client.MinecraftClient;
import net.minecraft.entity.Entity;
import net.minecraft.entity.LivingEntity;
import net.minecraft.util.hit.EntityHitResult;
import net.minecraft.util.hit.HitResult;
import net.minecraft.util.math.*;
import ru.sandfoxy.horizen.utils.animations.ContinualAnimation;

import java.util.Random;

import java.security.SecureRandom;

public class RotationUtils {
    private static final MinecraftClient mc = MinecraftClient.getInstance();
    private static final ContinualAnimation yawAnimation = new ContinualAnimation();
    private static final ContinualAnimation pitchAnimation = new ContinualAnimation();
    public static final SecureRandom secureRandom = new SecureRandom();

    public static Rotation getNeededRotations(Vec3d vec) {
        Vec3d eyes = mc.player.getEyePos();

        double diffX = vec.x - eyes.x;
        double diffZ = vec.z - eyes.z;
        double yaw = Math.toDegrees(Math.atan2(diffZ, diffX)) - 90F;

        double diffY = vec.y - eyes.y;
        double diffXZ = Math.sqrt(diffX * diffX + diffZ * diffZ);
        double pitch = -Math.toDegrees(Math.atan2(diffY, diffXZ));

        return Rotation.wrapped((float) yaw, (float) pitch);
    }

    public static Rotation LegitRotate(Entity target) {
        if (mc.player == null || mc.cameraEntity == null || target == null)
            return null;

        Rotation rotation = getNeededRotations(target.getEyePos());

        return new Rotation((float) rotation.yaw, (float) rotation.pitch);
    }
    public static Rotation getBowPredictionRotation(LivingEntity target, float arrowVelocity) {
        Vec3d shooterPos = mc.player.getEyePos();
        Vec3d targetPos = target.getEyePos();

        Vec3d targetVelocity = target.getVelocity();
        double distance = shooterPos.distanceTo(targetPos);

        float flightTime = getFlightTime(distance, arrowVelocity);

        // Предсказанная позиция с учётом скорости цели
        Vec3d futureTargetPos = targetPos.add(targetVelocity.multiply(flightTime));

        // Коррекция по высоте с учётом гравитации стрелы (~0.05f/tick^2)
        double gravity = 0.05;
        double drop = 0.5 * gravity * flightTime * flightTime;

        futureTargetPos = futureTargetPos.subtract(0, drop, 0);

        return getNeededRotations(futureTargetPos);
    }

    public static float getArrowVelocity(int charge) {
        float velocity = charge / 20.0f;
        velocity = (velocity * velocity + velocity * 2.0f) / 3.0f;
        return Math.min(velocity, 1.0f) * 3.0f; // max speed ~3 blocks/tick
    }

    private static float getFlightTime(double distance, float velocity) {
        return (float)(distance / velocity);
    }

    public static float getRandomFloat(float max, float min) {
        SecureRandom random = new SecureRandom();
        return random.nextFloat() * (max - min) + min;
    }

    public static Vec3d getRandomPointInBox(Box box) {
        Random random = new Random();

        // Calculate the center of the box
        double centerX = (box.minX + box.maxX) / 2.0;
        double centerY = (box.minY + box.maxY) / 2.0;
        double centerZ = (box.minZ + box.maxZ) / 2.0;

        // Calculate half the dimensions of the box
        double halfWidthX = (box.maxX - box.minX) / 2.0;
        double halfWidthY = (box.maxY - box.minY) / 2.0;
        double halfWidthZ = (box.maxZ - box.minZ) / 2.0;

        // Generate random offsets within half the box's dimensions
        double x = centerX + (random.nextDouble() * 2 - 1) * halfWidthX;
        double y = centerY + (random.nextDouble() * 2 - 1) * halfWidthY;
        double z = centerZ + (random.nextDouble() * 2 - 1) * halfWidthZ;

        return new Vec3d(x, y, z);
    }

    public static Rotation getEntityCenterRotation(Entity entity) {
        if (mc.player == null || mc.cameraEntity == null || entity == null)
            return null;

        Box entityBox = entity.getBoundingBox();

        Rotation req = RotationUtils.getNeededRotations(entityBox.getCenter());

        float f1 = (float) (mc.options.getMouseSensitivity().getValue() * 0.6F + 0.2F);
        float fac = f1 * f1 * f1 * 256.0F;

        return new Rotation(req.yaw, req.pitch);
    }

    public static float[] funtimeSnap(Entity entity) {
        if (mc.player == null || mc.cameraEntity == null || entity == null)
            return new float[] { 0.0F, 0.0F };

        HitResult hit = mc.crosshairTarget;
        if (hit != null && hit.getType() == HitResult.Type.ENTITY) {
            EntityHitResult entityHit = (EntityHitResult) hit;
            Entity target = entityHit.getEntity();
            if (target == entity){
                Vec3d hitPos = entityHit.getPos();
                return new float[] { mc.player.getYaw(), mc.player.getPitch(), (float) hitPos.x, (float) hitPos.y, (float) hitPos.z};
            }
        }

        Box entityBox = entity.getBoundingBox();
        Vec3d point = getRandomPointInBox(entityBox);

        Rotation req = RotationUtils.getNeededRotations(point);

        float f1 = (float) (mc.options.getMouseSensitivity().getValue() * 0.6F + 0.2F);
        float fac = f1 * f1 * f1 * 256.0F;

        float yaw = smoothRotation(mc.player.prevHeadYaw, req.yaw, 360f);
        float pitch = smoothRotation(mc.player.prevPitch, req.pitch, 180f);

        return new float[] { yaw, pitch, (float) point.x, (float) point.y, (float) point.z};
    }

    public static float[] getLegitRotations(Entity entity) {
        float f1 = (float) (mc.options.getMouseSensitivity().getValue() * 0.6F + 0.2F);
        float fac = f1 * f1 * f1 * 256.0F;

        Vec3d entityPos = entity.getPos();
        Vec3d localPos = mc.player.getPos();

        double x = entityPos.x - localPos.x;
        double z = entityPos.z - localPos.z;
        double y = entityPos.y + entity.getEyeHeight(entity.getPose())
                - (mc.player.getBoundingBox().minY
                        + (mc.player.getBoundingBox().maxY
                                - mc.player.getBoundingBox().minY));

        double d3 = MathHelper.sqrt((float) (x * x + z * z));
        float yaw = (float) (MathHelper.atan2(z, x) * 180.0 / Math.PI) - 90.0F;
        float pitch = (float) (-(MathHelper.atan2(y, d3) * 180.0 / Math.PI));
        yaw = smoothRotation(mc.player.prevHeadYaw, yaw, fac * getRandomFloat(0.9F, 1));
        pitch = smoothRotation(mc.player.prevPitch, pitch, fac * getRandomFloat(0.7F, 1));

        return new float[] { yaw, pitch };
    }

    public static float smoothRotation(float from, float to, float speed) {
        float f = MathHelper.wrapDegrees(to - from);

        if (f > speed) {
            f = speed;
        }

        if (f < -speed) {
            f = -speed;
        }

        return from + f;
    }

    public static class Rotation {
        public float yaw;
        public float pitch;

        public Rotation(float yaw, float pitch) {
            this.yaw = yaw;
            this.pitch = pitch;
        }

        public static float wrapDegrees(float degrees) {
            degrees = degrees % 360.f;
            if (degrees >= 180.0)
                degrees -= 360.f;
            if (degrees < -180.0)
                degrees += 360.f;
            return degrees;
        }

        static Rotation wrapped(float yaw, float pitch) {
            return new Rotation(wrapDegrees(yaw), wrapDegrees(pitch));
        }
    }
}
