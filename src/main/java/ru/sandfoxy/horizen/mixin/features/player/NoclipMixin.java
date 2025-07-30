package ru.sandfoxy.horizen.mixin.features.player;

import net.minecraft.client.network.ClientPlayerEntity;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;
import ru.sandfoxy.horizen.modules.ModuleManager;

@Mixin(ClientPlayerEntity.class)
public class NoclipMixin {
    @Inject(method = "pushOutOfBlocks", at = @At("HEAD"), cancellable = true)
    private void pushOutOfBlocks(double x, double z, CallbackInfo ci) {
        if (ModuleManager.getByName("NoClip").isEnabledRaw()) ci.cancel();
    }
}
