package ru.sandfoxy.horizen.mixin.imgui;

import com.llamalad7.mixinextras.injector.wrapmethod.WrapMethod;
import com.llamalad7.mixinextras.injector.wrapoperation.Operation;
import com.mojang.blaze3d.platform.GlStateManager;
import org.joml.Vector2d;
import org.spongepowered.asm.mixin.Mixin;
import ru.sandfoxy.horizen.imgui.ImGuiUils;
import ru.sandfoxy.horizen.imgui.screen.WindowScaling;

@Mixin(value = GlStateManager.class, remap = false)
public class GlStateManagerMixin {

    @WrapMethod(method = "_viewport")
    private static void viewport(int i, int j, int k, int l, Operation<Void> original) {
        if (!WindowScaling.isChanged()) {
            original.call(i, j, k, l);
            return;
        }
        original.call(WindowScaling.X_OFFSET, WindowScaling.Y_TOP_OFFSET, WindowScaling.WIDTH, WindowScaling.HEIGHT);
        ImGuiUils.menuCentred = false;
    }

    @WrapMethod(method = "_scissorBox")
    private static void scissorBox(int i, int j, int k, int l, Operation<Void> original) {
        Vector2d xy = WindowScaling.scalePoint(i, j);
        Vector2d wh = WindowScaling.scaleWidthHeight(k, l);
        original.call((int) xy.x, (int) xy.y, (int) wh.x, (int) wh.y);
    }

}
