package ru.sandfoxy.horizen.mixin.imgui;

import imgui.glfw.ImGuiImplGlfw;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;
import ru.sandfoxy.horizen.ModEntryPoint;

@Mixin(value = ImGuiImplGlfw.class, remap = false)
public class GlfwLockerMixin {
    @Inject(method = {"updateGamepads", "updateMouseCursor", "updateMousePosAndButtons"}, at = @At("HEAD"), cancellable = true)
    public void update(CallbackInfo ci) {
        if (ModEntryPoint.lockInput){
            ci.cancel();
        }
    }
}
