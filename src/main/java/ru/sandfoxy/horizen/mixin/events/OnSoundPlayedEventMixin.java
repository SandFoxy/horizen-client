package ru.sandfoxy.horizen.mixin.events;

import net.minecraft.client.network.ClientCommonNetworkHandler;
import net.minecraft.client.sound.SoundInstance;
import net.minecraft.client.sound.SoundManager;
import net.minecraft.client.world.ClientWorld;
import net.minecraft.util.ActionResult;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;
import ru.sandfoxy.horizen.events.OnSoundPlayedEvent;

@Mixin(SoundManager.class)
public class OnSoundPlayedEventMixin {
    @Inject(at = @At(value = "INVOKE", target = "Lnet/minecraft/client/sound/SoundSystem;play(Lnet/minecraft/client/sound/SoundInstance;)V"), method = "play(Lnet/minecraft/client/sound/SoundInstance;)V", cancellable = true)
    private void onShear(SoundInstance sound, CallbackInfo ci) {
        OnSoundPlayedEvent.EVENT.invoker().playSound(sound);
    }
}
