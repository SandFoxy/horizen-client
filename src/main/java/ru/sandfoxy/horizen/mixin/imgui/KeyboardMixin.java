package ru.sandfoxy.horizen.mixin.imgui;

import net.minecraft.client.Keyboard;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;
import ru.sandfoxy.horizen.ModEntryPoint;
import ru.sandfoxy.horizen.imgui.ClickGUI;

@Mixin(Keyboard.class)
public class KeyboardMixin {

    @Inject(method = "setup", at = @At("TAIL"))
    public void setup(long windowHandle, CallbackInfo ci) {
        ClickGUI.onGlfwInit(windowHandle);
    }

    @Inject(method = "onKey", at = @At("HEAD"), cancellable = true)
    public void onKeyPress(long window, int key, int scancode, int action, int modifiers, CallbackInfo ci) {
        if (ModEntryPoint.shouldCancelGameKeyboardInputs() && key != 300) {
            ci.cancel();
        }
    }


    @Inject(method = "onChar", at = @At("HEAD"), cancellable = true)
    public void onChar(long windowHandle, int codePoint, int modifiers, CallbackInfo ci) {
        if (ModEntryPoint.shouldCancelGameKeyboardInputs()) {
            ci.cancel();
        }
    }
}
