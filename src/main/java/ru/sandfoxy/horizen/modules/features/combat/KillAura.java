package ru.sandfoxy.horizen.modules.features.combat;

import imgui.ImGui;
import imgui.ImVec4;
import imgui.flag.ImGuiCol;
import imgui.flag.ImGuiStyleVar;
import imgui.flag.ImGuiWindowFlags;
import net.minecraft.client.MinecraftClient;
import net.minecraft.client.network.ClientPlayerEntity;
import net.minecraft.component.DataComponentTypes;
import net.minecraft.component.type.AttributeModifiersComponent;
import net.minecraft.entity.Entity;
import net.minecraft.entity.EquipmentSlot;
import net.minecraft.entity.LivingEntity;
import net.minecraft.entity.decoration.ArmorStandEntity;
import net.minecraft.entity.effect.StatusEffects;
import net.minecraft.entity.mob.MobEntity;
import net.minecraft.entity.passive.AnimalEntity;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;
import net.minecraft.item.Items;
import net.minecraft.util.Hand;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.Vec2f;
import net.minecraft.util.math.Vec3d;
import ru.sandfoxy.horizen.imgui.utils.ImGuiTexture;
import ru.sandfoxy.horizen.imgui.utils.TextureManager;
import ru.sandfoxy.horizen.modules.core.Module;
import ru.sandfoxy.horizen.modules.core.type.Checkbox;
import ru.sandfoxy.horizen.modules.core.type.ComboBox;
import ru.sandfoxy.horizen.modules.core.type.MultiComboBox;
import ru.sandfoxy.horizen.modules.core.type.Slider;
import ru.sandfoxy.horizen.modules.features.hack.FriendList;
import ru.sandfoxy.horizen.modules.features.misc.Pet;
import ru.sandfoxy.horizen.utils.render.RenderUtils;
import ru.sandfoxy.horizen.utils.math.RotationUtils;
import ru.sandfoxy.horizen.utils.math.WorldToScreen;
import ru.sandfoxy.horizen.utils.animations.Animation;
import ru.sandfoxy.horizen.utils.animations.Direction;
import ru.sandfoxy.horizen.utils.animations.impl.SmoothStepAnimation;

import java.awt.*;
import java.util.*;
import java.util.List;

import static net.minecraft.util.math.MathHelper.wrapDegrees;

import static ru.sandfoxy.horizen.utils.render.RenderUtils.*;
import static ru.sandfoxy.horizen.utils.math.RotationUtils.*;

public class KillAura extends Module {
    private static final MinecraftClient mc = MinecraftClient.getInstance();
    public static Entity targetEntity = null;

    private static Random random = new Random();
    private static float realYaw = 0.f;
    private static float realPitch = 0.f;

    private Animation scanAnimation;
    private Animation targetHudAnimation;
    private Animation healthBarAnimation;
    private float scanAnim = 0.f;
    private float targetHudWindowAlpha = 0.0f;
    private float lastHealth = 0.0f;

    private final Checkbox silent = new Checkbox(true, "Silent Rotation");
    private final Slider   attackDist = new Slider(3f, 1f, 5.f, "Attack Distance", Slider.SliderType.FLOAT);
    private final Slider   scanDist = new Slider(6f, 1f, 15.f, "Scan Distance", Slider.SliderType.INT);
    private final Checkbox onlyCritical = new Checkbox(true, "Only Critical");
    private final Checkbox resetSprint = new Checkbox(true, "Reset Sprint");
    private final Checkbox smartCritical = new Checkbox(true, "Smart Critical");
    private final Checkbox drawDistance = new Checkbox(true, "Draw Distance");
    private final ComboBox rotationMode = new ComboBox("Rotation", "Legit Snap", List.of("Legit Snap","FunTime Snap", "Legit"));
    private final ComboBox targethudMode = new ComboBox("TargetHud Mode", "Standard", List.of("Standard","Alternative"));
    private final ComboBox tatgetMode = new ComboBox("Target Mode", "Lock", List.of("Lock","Switch"));
    private final ComboBox targetSelectMode = new ComboBox("Target Select Mode", "Distance", List.of("Distance", "Health", "Rotation"));
    private final Slider legitFov = new Slider(90.f, 5.f, 180f, "Field Of View", Slider.SliderType.INT);
    private final MultiComboBox targets = new MultiComboBox("Targets", List.of("Players"), List.of("Players", "Friends","Invisibles", "Mobs", "Animals"));
    private List<Vec2f> visualizeRotation = new ArrayList<Vec2f>();
    private long lastAttackTime = 0;
    private long nextAttackDelay = 0;

    private float lastGroundY = 0;
    private long lastJumpTime = 0;
    private static final long JUMP_DELAY = 100; // 100ms delay after jump
    private static final float MIN_JUMP_HEIGHT = 0.1f; // Minimum height for jump crit

    public KillAura() {
        super("Attack Aura", CATEGORY.COMBAT, "Attack Aura", "KillAura\0Aura\0Kill Aura");
        this.addSetting(silent);
        this.addSetting(attackDist);
        this.addSetting(scanDist);
        this.addSetting(onlyCritical);
        this.addSetting(resetSprint);
        this.addSetting(drawDistance);
        this.addSetting(smartCritical);
        this.addSetting(rotationMode);
        this.addSetting(legitFov);
        this.addSetting(tatgetMode);
        this.addSetting(targetSelectMode);
        this.addSetting(targethudMode);
        this.addSetting(targets);

        smartCritical.visibleIf(onlyCritical::get);
        legitFov.visibleIf(() -> rotationMode.isEquals("Legit Snap"));
        
        // Initialize animations
        scanAnimation = new SmoothStepAnimation(5000, 1.0, Direction.FORWARDS);
        targetHudAnimation = new SmoothStepAnimation(200, 1.0, Direction.FORWARDS);
        healthBarAnimation = new SmoothStepAnimation(300, 1.0, Direction.FORWARDS);
    }


    public static float calculate2DDistance(Vec2f point1, Vec2f point2) {
        return (float) Math.sqrt(Math.pow(point2.x - point1.x, 2) + Math.pow(point2.y - point1.y, 2));
    }
    @Override
    public void startTick() {
        visualizeRotation.clear();
        if (mc.world == null || mc.player == null) {
            return; 
        }

        float closest_distance = scanDist.getFloat();
        Entity bestTarget = null;
        float bestValue = Float.MAX_VALUE;

        List<String> targetSet = targets.getList();
        for (Entity entity : mc.world.getEntities()) {
            if (!entity.isAlive() || entity == mc.player || !(entity instanceof LivingEntity livingEntity)) continue;

            // Ignore pet entity
            if (entity.getId() == Pet.petId) continue;

            if (entity instanceof ArmorStandEntity) continue;
            if (entity instanceof AnimalEntity) {
                if (!targetSet.contains("Animals")) continue;
            } else if (entity instanceof MobEntity && !targetSet.contains("Mobs")) continue;
            if (entity instanceof PlayerEntity && !targetSet.contains("Players")) continue;
            if (FriendList.friendList.getList().contains(entity.getName().getString()) && !targetSet.contains("Friends")) continue;
            if (entity.isInvisible() && !targetSet.contains("Invisibles")) continue;

            float distance = entity.distanceTo(mc.player);
            if (distance >= closest_distance) continue;

            float value = Float.MAX_VALUE;
            switch (targetSelectMode.getValue()) {
                case "Distance" -> value = distance;
                case "Health" -> value = ((LivingEntity) entity).getHealth();
                case "Rotation" -> {
                    Vec3d eyePos = mc.player.getEyePos();
                    Rotation angles = RotationUtils.getNeededRotations(entity.getBoundingBox().getCenter());
                    float yawDiff = Math.abs(wrapDegrees(angles.yaw - mc.player.getYaw()));
                    float pitchDiff = Math.abs(wrapDegrees(angles.pitch - mc.player.getPitch()));
                    value = yawDiff + pitchDiff;
                }
            }

            if (value < bestValue) {
                bestValue = value;
                bestTarget = entity;
            }
        }

        if (tatgetMode.getValue().equals("Lock") && targetEntity != null && targetEntity.isAlive() && targetEntity.distanceTo(mc.player) <= attackDist.getFloat()) {
            bestTarget = targetEntity;
        }

        targetEntity = bestTarget;

        realYaw = 0;
        realPitch = 0;

        if (targetEntity == null || targetEntity.distanceTo(mc.player) >= attackDist.getFloat()) { 
            return; 
        }

        Rotation rotation = null;

        switch (rotationMode.getValue()){
            case "Legit Snap":
                Vec2f screenCenter = new Vec2f((float) mc.getWindow().getWidth() / 2, (float) mc.getWindow().getHeight() / 2);
                Rotation bestRotation = null;
                float closestPoint = Float.MAX_VALUE;

                for (int i = 0; i <= 15; i++){
                    float[] legitSnapRotation = RotationUtils.funtimeSnap(targetEntity);
                    Rotation newRot = new Rotation(legitSnapRotation[0], legitSnapRotation[1]);

                    Vec3d playerOnScreen = WorldToScreen.w2s(new Vec3d(legitSnapRotation[2],legitSnapRotation[3], legitSnapRotation[4]));
                    if (playerOnScreen == null) continue;

                    Vec2f playerPos = new Vec2f((float) playerOnScreen.x, (float) playerOnScreen.y);

                    visualizeRotation.add(playerPos);

                    float distance = calculate2DDistance(screenCenter, playerPos);
                    if (distance <= legitFov.getFloat() && distance < closestPoint){
                        closestPoint = distance;
                        bestRotation = newRot;
                    }
                }

                rotation = bestRotation;
                break;
            case "FunTime Snap":
                float[] ftSnap = RotationUtils.funtimeSnap(targetEntity);
                rotation = new Rotation(ftSnap[0], ftSnap[1]);
                break;
            case "Legit":
                float[] legitRotation = RotationUtils.getLegitRotations(targetEntity);
                rotation = new Rotation(legitRotation[0], legitRotation[1]);
                break;
        }

        if (rotation == null) {
            return;
        }

        realPitch = mc.player.getPitch();
        realYaw = mc.player.getYaw();

        float attackStrength = mc.player.getAttackCooldownProgress(0.0f);

        if (!rotationMode.getValue().contains("Snap")){
            mc.player.setYaw(rotation.yaw);
            mc.player.setPitch(rotation.pitch);
        }

        if (mc.player.getMainHandStack().getItem() == Items.MACE && calculateFallDistance() > 1f && attackStrength >= 0.4f && mc.player.getVelocity().y < 0f){
            if (rotationMode.getValue().contains("Snap")){
                mc.player.setYaw(rotation.yaw);
                mc.player.setPitch(rotation.pitch);
            }

            mc.interactionManager.attackEntity(mc.player, targetEntity);
            mc.player.swingHand(Hand.MAIN_HAND);
            return;
        }

        if (attackStrength >= 0.9f && (willDealCritical() || mc.player.isGliding() ) && (mc.player.getMainHandStack().getItem() != Items.MACE || mc.player.getVelocity().y > 0.f)) {
            long currentTime = System.currentTimeMillis();
            if (currentTime - lastAttackTime < nextAttackDelay) {
                return;
            }
            lastAttackTime = currentTime;
            nextAttackDelay = (long) (200 + random.nextFloat() * (500 - 100));

            if (rotationMode.getValue().contains("Snap")){
                mc.player.setYaw(rotation.yaw);
                mc.player.setPitch(rotation.pitch);
            }

            // Smart critical sprint management is now handled in willDealCritical()
            // Only reset sprint for critical hits when not using smart crits
            if (!smartCritical.getValue() && mc.player.isSprinting() && !mc.player.isOnGround() &&
                !mc.player.isClimbing() && !mc.player.isTouchingWater() && 
                !mc.player.hasStatusEffect(StatusEffects.BLINDNESS) && !mc.player.hasVehicle() && 
                calculateFallDistance() > 0.0F && !mc.player.isUsingRiptide() && 
                targetEntity instanceof LivingEntity) {
                mc.player.setSprinting(false);
            }

            mc.interactionManager.attackEntity(mc.player, targetEntity);
            mc.player.swingHand(Hand.MAIN_HAND);
        }
    }

    private boolean blockAbove(Entity entity) {
        if (mc.world == null || entity == null) return false;

        Vec3d playerBlock = entity.getPos().add(0, entity.getHeight(), 0);
        BlockPos blockPos = BlockPos.ofFloored(playerBlock);
        
        return !mc.world.getBlockState(blockPos).isAir();
    }

    private boolean willDealCritical() {
        if (!onlyCritical.getValue()) return true;

        if (resetSprint.getValue()){
            if (mc.player.isSprinting() && !mc.player.isOnGround()) {
                mc.player.setSprinting(false);
            }
        }

        float attackCooldown = mc.player.getAttackCooldownProgress(0.0f);

        if (smartCritical.getValue() && targetEntity instanceof LivingEntity target) {
            float targetHealth = target.getHealth();

            float baseDamage = calculateBaseDamage(mc.player.getMainHandStack(), target);

            if (targetHealth + target.getAbsorptionAmount() <= baseDamage && attackCooldown >= 0.424f) {
                return true;
            }
        }

        if (attackCooldown < 0.848f) return false;

        if (mc.player.isOnGround()) return false;

        if (mc.player.isClimbing()) return false;

        if (mc.player.isTouchingWater()) return false;

        if (mc.player.hasStatusEffect(StatusEffects.BLINDNESS)) return false;

        if (mc.player.hasStatusEffect(StatusEffects.SLOW_FALLING)) return false;

        if (mc.player.hasVehicle()) return false;

        if (calculateFallDistance() <= 0.0F || mc.player.getVelocity().y >= -0.1f) return false;

        if (mc.player.isUsingRiptide()) return false;

        if (smartCritical.getValue()) {
            if (blockAbove(targetEntity)) return true;
        }

        return true;
    }

    public static float calculateBaseDamage(ItemStack stack) {
        AttributeModifiersComponent modifiers = stack.get(DataComponentTypes.ATTRIBUTE_MODIFIERS);
        if (modifiers != null) {
            return (float) modifiers.modifiers().stream()
                    .filter(modifier -> modifier.modifier().idMatches(Item.BASE_ATTACK_DAMAGE_MODIFIER_ID))
                    .filter(modifier -> modifier.slot().matches(EquipmentSlot.MAINHAND))
                    .mapToDouble(modifier -> modifier.modifier().value())
                    .sum();
        }
        return 1.0F;
    }

    
    public static float calculateBaseDamage(ItemStack stack, Entity entity) {
        float baseDamage = 1.0F;
        AttributeModifiersComponent modifiers = stack.get(DataComponentTypes.ATTRIBUTE_MODIFIERS);
        if (modifiers != null) {
            baseDamage = (float) modifiers.modifiers().stream()
                    .filter(modifier -> modifier.modifier().idMatches(Item.BASE_ATTACK_DAMAGE_MODIFIER_ID))
                    .filter(modifier -> modifier.slot().matches(EquipmentSlot.MAINHAND))
                    .mapToDouble(modifier -> modifier.modifier().value())
                    .sum();
        }

        if (!(entity instanceof LivingEntity livingEntity)) {
            return baseDamage;
        }

        float armor = livingEntity.getArmor();
        float armorToughness = (float) livingEntity.getAttributeValue(net.minecraft.entity.attribute.EntityAttributes.ARMOR_TOUGHNESS);

        float armorProtection = Math.min(20.0F, Math.max(armor / 5.0F, armor - baseDamage / (2.0F + armorToughness / 4.0F)));

        float damageAfterArmor = baseDamage * (1.0F - armorProtection / 25.0F);

        if (livingEntity.hasStatusEffect(net.minecraft.entity.effect.StatusEffects.RESISTANCE)) {
            int resistanceLevel = livingEntity.getStatusEffect(net.minecraft.entity.effect.StatusEffects.RESISTANCE).getAmplifier() + 1;
            float resistanceReduction = resistanceLevel * 0.2F;
            damageAfterArmor *= (1.0F - Math.min(resistanceReduction, 1.0F));
        }
        
        return Math.max(0.0F, damageAfterArmor);
    }

    private double lastY = 0;
    private double fallDist = 0;

    private double calculateFallDistance() {
        ClientPlayerEntity player = mc.player;

        // Обновляем lastY при первом тике или после касания земли
        if (player.isOnGround()) {
            fallDist = 0;
            lastY = player.getY();
        } else {
            double currentY = player.getY();
            double delta = lastY - currentY;
            if (delta > 0) {
                fallDist += delta; // накапливаем спуск
            }
            lastY = currentY;
        }

        return fallDist;
    }

    @Override
    public void endTick(){
        if (mc.player == null || (realYaw == 0 || realPitch == 0)) { return; }

        if (silent.getValue()){
            mc.player.setPitch(realPitch);
            mc.player.setYaw(realYaw);
        }
    }


    @Override
    public void onDraw(){
        if (rotationMode.getValue().equals("Legit Snap")){
//            for (Vec2f dot : visualizeRotation){
//                ImGui.getBackgroundDrawList().addCircleFilled(dot.x, dot.y, 3, ConvertColor(Color.white));
//                ImGui.getBackgroundDrawList().addCircleFilled(dot.x, dot.y, 2f, ConvertColor(Color.black));
//            }

            ImGui.getBackgroundDrawList().addCircle(ImGui.getIO().getDisplaySizeX() / 2, ImGui.getIO().getDisplaySizeY() / 2, legitFov.getFloat(), ConvertColor(Color.BLACK),0,2);
            ImGui.getBackgroundDrawList().addCircle(ImGui.getIO().getDisplaySizeX() / 2, ImGui.getIO().getDisplaySizeY() / 2, legitFov.getFloat(), ConvertColor(Color.WHITE));
        }

        if (mc.world == null) return;

        if (targetEntity == null) { return; }

        if (scanAnimation.isDone()) {
            scanAnimation.setDirection(scanAnimation.getDirection() == Direction.FORWARDS ? Direction.BACKWARDS : Direction.FORWARDS);
            scanAnimation.reset();
        }
        scanAnim = scanAnimation.getOutput().floatValue() * (float) (targetEntity.getBoundingBox().getLengthY() * 5);

        Vec3d playerCenter = WorldToScreen.w2s(RenderUtils.interpolate(targetEntity, WorldToScreen.tickDelta).add(0, -0.5f, 0));

        if (this.drawDistance.getValue()){
            RenderUtils.Draw3DRing(targetEntity.getPos(),3, 128, ConvertColor(Color.red), -1);
        }

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

    @Override
    public void onGui(){
        Entity localTarget = targetEntity;
        int window_flags = ImGuiWindowFlags.NoCollapse | ImGuiWindowFlags.NoNavFocus | ImGuiWindowFlags.AlwaysAutoResize | ImGuiWindowFlags.NoNavInputs |ImGuiWindowFlags.NoResize | ImGuiWindowFlags.NoSavedSettings;

        if (mc.inGameHud.getChatHud().isChatFocused()) localTarget = mc.player;

        // Update target HUD animation
        if (localTarget != null) {
            if (targetHudAnimation.getDirection() == Direction.BACKWARDS) {
                targetHudAnimation.setDirection(Direction.FORWARDS);
                targetHudAnimation.reset();
            }
        } else {
            if (targetHudAnimation.getDirection() == Direction.FORWARDS) {
                targetHudAnimation.setDirection(Direction.BACKWARDS);
                targetHudAnimation.reset();
            }
        }
        targetHudWindowAlpha = targetHudAnimation.getOutput().floatValue();

        if (localTarget == null || !(localTarget instanceof LivingEntity)) return;
        if (targetHudWindowAlpha <= 0.01f) return;

        if (!mc.inGameHud.getChatHud().isChatFocused()) {
            window_flags |= ImGuiWindowFlags.NoMove | ImGuiWindowFlags.NoNavFocus;
        }

        LivingEntity livingEntity = (LivingEntity) localTarget;
        float currentHealth = livingEntity.getHealth() + livingEntity.getAbsorptionAmount();
        float maxHealth = livingEntity.getMaxHealth() + livingEntity.getAbsorptionAmount();

        if ((livingEntity.getMaxHealth() + livingEntity.getAbsorptionAmount()) > 20) ImGui.pushStyleColor(ImGuiCol.PlotHistogram, ImGui.getColorU32(1,1,0,0.7f));

        if (targethudMode.getValue().equals("Standard")){
            ImGui.setNextWindowBgAlpha(targetHudWindowAlpha * 0.9f);
            ImGui.pushStyleVar(ImGuiStyleVar.Alpha, targetHudWindowAlpha);
            ImGui.begin("Target Hud", window_flags);
            ImGui.text(String.format("%s (%#.2f / %#.2f)", localTarget.getName().getString(), currentHealth, maxHealth).replace(",", "."));

            // Animate health bar
            if (lastHealth != currentHealth) {
                healthBarAnimation.reset();
                lastHealth = currentHealth;
            }
            float animatedHealth = lastHealth + (currentHealth - lastHealth) * healthBarAnimation.getOutput().floatValue();
            ImGui.progressBar(animatedHealth / maxHealth);

            ImGui.end();
            ImGui.popStyleVar();
        }else {
            ImGui.setNextWindowBgAlpha(targetHudWindowAlpha * 0.9f);
            ImGui.pushStyleVar(ImGuiStyleVar.Alpha, targetHudWindowAlpha);
            ImGui.begin("Target Hud", window_flags);
            float healthPercent = (currentHealth / maxHealth) * 100.0f;
            ImGui.text(String.format("%s     %.0f%%", localTarget.getName().getString(), healthPercent));

            // Animate health bar
            if (lastHealth != currentHealth) {
                healthBarAnimation.reset();
                lastHealth = currentHealth;
            }
            float animatedHealth = lastHealth + (currentHealth - lastHealth) * healthBarAnimation.getOutput().floatValue();
            String overlay = String.format("%.1f / %.1f", animatedHealth, maxHealth).replace(",", ".");
            ImGui.progressBar(animatedHealth / maxHealth, 0, 0, overlay);

            ImGui.end();
            ImGui.popStyleVar();
        }
        if ((livingEntity.getMaxHealth() + livingEntity.getAbsorptionAmount()) > 20) ImGui.popStyleColor();
    }

    @Override
    public void onDisable() {
        targetEntity = null;
        scanAnimation.reset();
        targetHudAnimation.reset();
        healthBarAnimation.reset();
        lastHealth = 0.0f;
    }

}
