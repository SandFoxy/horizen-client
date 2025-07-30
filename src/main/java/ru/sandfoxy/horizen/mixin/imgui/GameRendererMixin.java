package ru.sandfoxy.horizen.mixin.imgui;

import com.llamalad7.mixinextras.injector.wrapoperation.Operation;
import com.llamalad7.mixinextras.injector.wrapoperation.WrapOperation;
import net.minecraft.client.render.GameRenderer;
import org.joml.Matrix4f;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import ru.sandfoxy.horizen.imgui.screen.WindowScaling;

@Mixin(GameRenderer.class)
public abstract class GameRendererMixin {

    @WrapOperation(
            method = "getBasicProjectionMatrix",
            at = @At(
                    value = "INVOKE",
                    target = "Lorg/joml/Matrix4f;perspective(FFFF)Lorg/joml/Matrix4f;",
                    remap = false
            )
    )
    private Matrix4f overridePerspectiveProjection(
            Matrix4f instance,
            float fovy,
            float aspect,
            float zNear,
            float zFar,
            Operation<Matrix4f> original
    ) {
        return original.call(instance, fovy, (float) WindowScaling.WIDTH / WindowScaling.HEIGHT, zNear, zFar);
    }
}
