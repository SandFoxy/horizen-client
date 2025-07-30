package ru.sandfoxy.horizen.mixin.utils;

import net.minecraft.client.gui.hud.ChatHud;
import net.minecraft.client.gui.hud.ChatHudLine;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;
import ru.sandfoxy.horizen.utils.ChatHelper;


@Mixin(ChatHud.class)
public class ChatLoggerMixin {
    @Inject(method = "logChatMessage", at = @At("HEAD"), cancellable = true)
    public void sendMessage(ChatHudLine message, CallbackInfo ci) {
        if (ChatHelper.stopLogging) ci.cancel();
    }
}