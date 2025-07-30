package ru.sandfoxy.horizen.mixin.imgui;

import net.minecraft.client.gl.Framebuffer;
import net.minecraft.client.gl.PostEffectPass;
import net.minecraft.client.render.FrameGraphBuilder;
import net.minecraft.client.util.Handle;
import net.minecraft.util.Identifier;
import org.joml.Matrix4f;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;
import ru.sandfoxy.horizen.imgui.screen.WindowScaling;

import java.util.Map;

@Mixin(PostEffectPass.class)
public class PostEffectPassMixin {

    @Inject(method = "render", at = @At("HEAD"), cancellable = true)
    public void render(FrameGraphBuilder builder, Map<Identifier, Handle<Framebuffer>> handles, Matrix4f projectionMatrix, CallbackInfo ci) {
        if (WindowScaling.DISABLE_POST_PROCESSORS) {
            ci.cancel();
        }
    }
}
