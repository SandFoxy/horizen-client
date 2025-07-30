package ru.sandfoxy.horizen.mixin.imgui;

import com.mojang.blaze3d.systems.RenderSystem;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;
import ru.sandfoxy.horizen.imgui.ClickGUI;

@Mixin(value = RenderSystem.class, remap = false)
public class TailRenderMixin {
    @Inject(method = "flipFrame", at = @At("HEAD"))
    private static void onFlipFrame(CallbackInfo ci) {
        // If you're not managing a Profiler yourself, remove these two lines
        //Profiler.get().push("ImGui Render");
        ClickGUI.onFrameRender();
        //Profiler.get().pop();
    }
}
