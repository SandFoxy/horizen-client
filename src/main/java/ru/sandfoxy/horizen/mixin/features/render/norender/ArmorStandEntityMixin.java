package ru.sandfoxy.horizen.mixin.features.render.norender;

import net.minecraft.entity.Entity;
import net.minecraft.entity.decoration.ArmorStandEntity;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.util.ActionResult;
import net.minecraft.util.Hand;
import net.minecraft.util.math.Vec3d;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;
import ru.sandfoxy.horizen.modules.ModuleManager;
import ru.sandfoxy.horizen.modules.features.render.NoRender;

@Mixin(ArmorStandEntity.class)
public class ArmorStandEntityMixin {

    @Inject(method = "interactAt", at = @At("HEAD"), cancellable = true)
    private void disableInteraction(PlayerEntity player, Vec3d hitPos, Hand hand, CallbackInfoReturnable<ActionResult> cir) {
        if (NoRender.renderFilter.getList().contains("Armor Stand") && ModuleManager.getByName("NoRender").isEnabledRaw())  cir.setReturnValue(ActionResult.FAIL);
    }

    @Inject(method = "damage", at = @At("HEAD"), cancellable = true)
    private void disableDamage(CallbackInfoReturnable<Boolean> cir) {
        if (NoRender.renderFilter.getList().contains("Armor Stand") && ModuleManager.getByName("NoRender").isEnabledRaw())  cir.setReturnValue(false);
    }

    @Inject(method = "isPushable", at = @At("HEAD"), cancellable = true)
    private void disablePushable(CallbackInfoReturnable<Boolean> cir) {
        if (NoRender.renderFilter.getList().contains("Armor Stand") && ModuleManager.getByName("NoRender").isEnabledRaw())  cir.setReturnValue(false);
    }

    @Inject(method = "pushAway", at = @At("HEAD"), cancellable = true)
    private void disablePushAway(Entity entity, CallbackInfo ci) {
        if (NoRender.renderFilter.getList().contains("Armor Stand") && ModuleManager.getByName("NoRender").isEnabledRaw())  ci.cancel();
    }

    @Inject(method = "canHit", at = @At("HEAD"), cancellable = true)
    private void disableCanHit(CallbackInfoReturnable<Boolean> cir) {
        if (NoRender.renderFilter.getList().contains("Armor Stand") && ModuleManager.getByName("NoRender").isEnabledRaw())  cir.setReturnValue(false);
    }
}