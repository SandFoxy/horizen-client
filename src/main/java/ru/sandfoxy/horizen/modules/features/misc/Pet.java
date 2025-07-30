package ru.sandfoxy.horizen.modules.features.misc;

import net.minecraft.client.MinecraftClient;
import net.minecraft.entity.Entity;
import net.minecraft.entity.EntityType;
import net.minecraft.entity.passive.*;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.Box;
import net.minecraft.util.math.MathHelper;
import net.minecraft.util.math.Vec3d;
import ru.sandfoxy.horizen.modules.core.Module;
import ru.sandfoxy.horizen.modules.core.type.ComboBox;
import ru.sandfoxy.horizen.modules.features.combat.KillAura;
import ru.sandfoxy.horizen.utils.math.RotationUtils;

import java.util.List;
import java.util.Random;

public class Pet extends Module {
    private final MinecraftClient mc = MinecraftClient.getInstance();
    private AnimalEntity petEntity = null;
    public static int petId = 0;
    private final ComboBox petType = new ComboBox("Pet Type", "Frog", List.of("Frog", "Wolf", "Fox", "Pig"));
    private Vec3d lastPos = Vec3d.ZERO;
    private Vec3d targetPos = Vec3d.ZERO;
    private static final double SMOOTH_FACTOR = 0.15;
    private static final double FOLLOW_DISTANCE = 3.0;
    private static final double TELEPORT_DISTANCE = 10.0;
    private static final double ATTACK_DISTANCE = 2.0;
    private static final double CIRCLE_RADIUS = 2.0;
    private double circleAngle = 0;
    private final Random random = new Random();
    private boolean isJumping = false;
    private int jumpCooldown = 0;

    private String lastPet = "";
    public Pet() {
        super("Pet", CATEGORY.MISC, "Creates a pet that follows you. Choose between Frog, Wolf, or Fox.");
        this.addSetting(petType);
    }

    @Override
    public void onEnable() {
        if (mc.world == null || mc.player == null) return;
        spawnPet();

        lastPet = petType.getValue();
    }

    private void spawnPet() {
        if (mc.world == null || mc.player == null) return;

        AnimalEntity pet;
        switch (petType.getValue()) {
            case "Frog":
                pet = new FrogEntity(EntityType.FROG, mc.world);
                break;
            case "Wolf":
                pet = new WolfEntity(EntityType.WOLF, mc.world);
                break;
            case "Fox":
                pet = new FoxEntity(EntityType.FOX, mc.world);
                break;
            case "Pig":
                pet = new PigEntity(EntityType.PIG, mc.world);
                break;
            default:
                return;
        }

        pet.setBoundingBox(new Box(0, 0, 0, 0, 0, 0));
        pet.noClip = true;

        Entity worldEntity = mc.world.getEntityById(petId);
        if (worldEntity != null){
            worldEntity.remove(Entity.RemovalReason.UNLOADED_WITH_PLAYER);
        }

        mc.world.addEntity(pet);
        Vec3d spawnPos = mc.player.getPos().add(0, 0, 2);
        pet.setPosition(spawnPos);
        this.petEntity = pet;
        Pet.petId = pet.getId();
        this.lastPos = spawnPos;
        this.targetPos = spawnPos;
    }

    private boolean isOnGround(Vec3d pos) {
        if (mc.world == null) return false;
        BlockPos blockPos = new BlockPos((int)pos.x, (int)pos.y - 1, (int)pos.z);
        return !mc.world.getBlockState(blockPos).isAir();
    }

    private Vec3d getCirclePosition(Vec3d center, double radius, double angle) {
        return new Vec3d(
            center.x + Math.cos(angle) * radius,
            center.y,
            center.z + Math.sin(angle) * radius
        );
    }

    @Override
    public void startTick() {
        if (mc.world == null || mc.player == null) return;

        if (petEntity == null || mc.world.getEntityById(petId) == null || !mc.world.getEntityById(petId).equals(petEntity) || !lastPet.equals(petType.getValue())) {
            spawnPet();
            lastPet = petType.getValue();
            return;
        }

        Vec3d playerPos = mc.player.getPos();
        Vec3d petPos = petEntity.getPos();
        double distance = petPos.distanceTo(playerPos);

        // Handle KillAura target
        if (KillAura.targetEntity != null) {
            Vec3d targetPos = KillAura.targetEntity.getPos();
            double targetDistance = petPos.distanceTo(targetPos);

            // Update circle angle
            circleAngle += 0.1;
            if (circleAngle > Math.PI * 2) circleAngle = 0;

            // Jump attack
            if (jumpCooldown <= 0 && targetDistance < ATTACK_DISTANCE * 2 && !isJumping) {
                isJumping = true;
                jumpCooldown = 20; // 1 second cooldown
            }

            if (isJumping) {
                Vec3d jumpTarget = targetPos.add(0, 1, 0);
                Vec3d jumpDirection = jumpTarget.subtract(petPos).normalize();
                targetPos = petPos.add(jumpDirection.multiply(0.3));
                
                // End jump if reached target height
                if (petPos.y >= targetPos.y) {
                    isJumping = false;
                }
            } else {
                // Circle around target
                targetPos = getCirclePosition(targetPos, CIRCLE_RADIUS, circleAngle);
            }

            // Keep pet on ground when not jumping
            if (!isJumping && isOnGround(targetPos)) {
                targetPos = new Vec3d(targetPos.x, targetPos.y, targetPos.z);
            }
        } else {
            // Normal following behavior
            if (distance > TELEPORT_DISTANCE) {
                Vec3d teleportPos = playerPos.add(0, 0, 2);
                petEntity.setPosition(teleportPos);
                lastPos = teleportPos;
                targetPos = teleportPos;
                return;
            }

            if (distance > FOLLOW_DISTANCE) {
                Vec3d direction = playerPos.subtract(petPos).normalize();
                targetPos = playerPos.subtract(direction.multiply(2));
                
                if (isOnGround(playerPos)) {
                    targetPos = new Vec3d(targetPos.x, playerPos.y, targetPos.z);
                }
            }
        }

        // Smooth movement
        Vec3d newPos = new Vec3d(
            MathHelper.lerp(SMOOTH_FACTOR, lastPos.x, targetPos.x),
            MathHelper.lerp(SMOOTH_FACTOR, lastPos.y, targetPos.y),
            MathHelper.lerp(SMOOTH_FACTOR, lastPos.z, targetPos.z)
        );
        
        petEntity.setPosition(newPos);
        lastPos = newPos;

        // Handle rotation
        float[] rot;
        if (KillAura.targetEntity != null) {
            rot = RotationUtils.getLegitRotations(KillAura.targetEntity);
        } else {
            rot = new float[]{mc.player.prevYaw, mc.player.prevPitch};
        }
        
        RotationUtils.Rotation targetRot = new RotationUtils.Rotation(rot[0], rot[1]);
        float newYaw = smoothAngle(petEntity.getYaw(), targetRot.yaw, 10.0f);
        float newPitch = smoothAngle(petEntity.getPitch(), targetRot.pitch, 10.0f);

        petEntity.prevYaw = petEntity.getYaw();
        petEntity.setYaw(newYaw);
        petEntity.setPitch(newPitch);
        petEntity.prevBodyYaw = petEntity.bodyYaw;
        petEntity.bodyYaw = newYaw;
        petEntity.headYaw = newYaw;

        // Update animation
        if (isJumping || distance > FOLLOW_DISTANCE) {
            float speed = (float) (distance * 0.1f);
            petEntity.limbAnimator.updateLimbs(speed, 1.0f, 1.0f);
        }

        // Update cooldowns
        if (jumpCooldown > 0) {
            jumpCooldown--;
        }
    }

    private float smoothAngle(float from, float to, float maxTurn) {
        float delta = MathHelper.wrapDegrees(to - from);
        delta = MathHelper.clamp(delta, -maxTurn, maxTurn);
        return from + delta;
    }

    @Override
    public void endTick() {
    }

    @Override
    public void onDisable() {
        if (mc.world == null) return;

        Entity pet = mc.world.getEntityById(petId);
        if (pet != null) {
            pet.remove(Entity.RemovalReason.UNLOADED_WITH_PLAYER);
        }

        petEntity = null;
        Pet.petId = 0;
    }
}
