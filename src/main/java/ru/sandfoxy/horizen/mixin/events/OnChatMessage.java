package ru.sandfoxy.horizen.mixin.events;


import net.minecraft.client.gui.screen.ChatScreen;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;
import ru.sandfoxy.horizen.events.OnCheatCommandMessage;

import static ru.sandfoxy.horizen.modules.features.misc.Unhook.cheatUnloaded;

@Mixin(ChatScreen.class)
public class OnChatMessage {
    @Inject(method = "sendMessage", at = @At("HEAD"), cancellable = true)
    public void sendMessage(String message, boolean toHud, CallbackInfo ci) {
        if (message.startsWith(".") && !cheatUnloaded){
            OnCheatCommandMessage.EVENT.invoker().sendMessage(message.substring(1),toHud);
            ci.cancel();
        }
    }
}