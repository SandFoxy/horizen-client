package ru.sandfoxy.horizen.modules.features.combat;


import net.minecraft.client.MinecraftClient;
import net.minecraft.entity.Entity;
import net.minecraft.entity.effect.StatusEffects;
import net.minecraft.util.Hand;
import net.minecraft.util.hit.EntityHitResult;
import net.minecraft.util.hit.HitResult;
import ru.sandfoxy.horizen.modules.core.Module;
import ru.sandfoxy.horizen.modules.core.type.Checkbox;

public class Triggerbot extends Module {
    private MinecraftClient mc = MinecraftClient.getInstance();
    private float attackCooldown = 0.0f;
    private final Checkbox onlyCrit = new Checkbox(true,"Only Crits");


    public Triggerbot() {
        super("Triggerbot", CATEGORY.COMBAT, "Triggerbot");
        this.addSetting(onlyCrit);
    }

    @Override
    public void startTick() {
        if (mc.world == null || mc.player == null || mc.crosshairTarget == null || !mc.isWindowFocused()) return;

        HitResult hit = mc.crosshairTarget;
        if (hit != null && hit.getType() == HitResult.Type.ENTITY) {
            EntityHitResult entityHit = (EntityHitResult) hit;
            Entity target = entityHit.getEntity();

            if (target.isAlive()) {
                float attackStrength = mc.player.getAttackCooldownProgress(0.0f);
                if (attackStrength >= 0.9f && (!(onlyCrit.getValue()) || isCriticalHit())) {
                    mc.interactionManager.attackEntity(mc.player, target);
                    mc.player.swingHand(Hand.MAIN_HAND);
                }
            }
        }
    }

    public boolean isCriticalHit() {
        if (mc.player == null) return false;

        return !mc.player.isOnGround()
                && mc.player.getVelocity().y < -0.25f
                && !mc.player.isTouchingWater()
                && !mc.player.hasStatusEffect(StatusEffects.BLINDNESS)
                && !mc.player.hasStatusEffect(StatusEffects.SLOW_FALLING)
                && !mc.player.hasVehicle()
                && !mc.player.getAbilities().flying
                && !mc.player.isClimbing()
                && mc.player.getAttackCooldownProgress(0.5f) > 0.848f;
    }
}
