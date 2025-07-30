package ru.sandfoxy.horizen.mixin.features.player.nodelay;

import net.minecraft.client.MinecraftClient;
import net.minecraft.entity.LivingEntity;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;
import ru.sandfoxy.horizen.modules.ModuleManager;
import ru.sandfoxy.horizen.modules.features.player.NoDelay;
import ru.sandfoxy.horizen.modules.features.player.Viewmodel;

@Mixin(LivingEntity.class)
public class NoDelayMixin {
    @Shadow
    private int jumpingCooldown;

    @Inject(method = "tickMovement", at = @At("HEAD"))
    public void tickMovement(CallbackInfo ci) {
        LivingEntity self = (LivingEntity) (Object) this;
        if (self != MinecraftClient.getInstance().player) return;

        if (ModuleManager.getByName("NoDelay").isEnabled() && NoDelay.delayTypes.getList().contains("Jump Delay")){
            this.jumpingCooldown = 0;
        }
    }

    @Inject(method = "getHandSwingDuration", at = @At("HEAD"), cancellable = true)
    private void onGetHandSwingDuration(CallbackInfoReturnable<Integer> cir) {
        if (!ModuleManager.getByName("View model").isEnabled()) return;

        cir.setReturnValue(Viewmodel.handSpeedSwing.getInt() + 2);
        cir.cancel();
    }
}
