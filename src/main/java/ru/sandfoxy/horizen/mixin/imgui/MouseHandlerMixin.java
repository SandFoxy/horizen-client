package ru.sandfoxy.horizen.mixin.imgui;

import com.llamalad7.mixinextras.injector.wrapmethod.WrapMethod;
import com.llamalad7.mixinextras.injector.wrapoperation.Operation;
import com.llamalad7.mixinextras.injector.wrapoperation.WrapOperation;
import net.minecraft.client.Mouse;
import net.minecraft.client.util.Window;
import org.joml.Vector2d;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;
import ru.sandfoxy.horizen.ModEntryPoint;
import ru.sandfoxy.horizen.imgui.screen.WindowScaling;

@Mixin(Mouse.class)
public class MouseHandlerMixin {

    @Inject(method = "onMouseButton", at = @At("HEAD"), cancellable = true)
    public void onMouseButton(long window, int button, int action, int mods, CallbackInfo ci) {
        if (ModEntryPoint.menuOpened) {
            ci.cancel();
        }
    }

    @Inject(method = "lockCursor", at = @At("HEAD"), cancellable = true)
    public void lockCursor(CallbackInfo ci) {
        if (ModEntryPoint.menuOpened) {
            ci.cancel();
        }
    }

    @WrapMethod(method = "onCursorPos")
    public void onCursorPos(long window, double x, double y, Operation<Void> original) {
        if (ModEntryPoint.menuOpened) {
            return;
        }

        Vector2d scaled = WindowScaling.unscalePoint(x, y);
        original.call(window, scaled.x, scaled.y);
    }

    @WrapOperation(method = "lockCursor", at = @At(value = "INVOKE", target = "Lnet/minecraft/client/util/Window;getWidth()I"))
    public int calculateDoubledCentreX(Window instance, Operation<Integer> original) {
        return (WindowScaling.X_OFFSET + (WindowScaling.WIDTH / 2)) * 2;
    }

    @WrapOperation(method = "lockCursor", at = @At(value = "INVOKE", target = "Lnet/minecraft/client/util/Window;getHeight()I"))
    public int calculateDoubledCentreY(Window instance, Operation<Integer> original) {
        return (WindowScaling.Y_OFFSET + (WindowScaling.HEIGHT / 2)) * 2;
    }
}
