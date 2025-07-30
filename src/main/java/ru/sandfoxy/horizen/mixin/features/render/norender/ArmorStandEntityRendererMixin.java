package ru.sandfoxy.horizen.mixin.features.render.norender;

import net.minecraft.client.render.VertexConsumerProvider;
import net.minecraft.client.render.entity.ArmorStandEntityRenderer;
import net.minecraft.client.render.entity.state.ArmorStandEntityRenderState;
import net.minecraft.client.util.math.MatrixStack;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;
import ru.sandfoxy.horizen.modules.ModuleManager;
import ru.sandfoxy.horizen.modules.features.render.NoRender;

@Mixin(ArmorStandEntityRenderer.class)
public class ArmorStandEntityRendererMixin {
    @Inject(method = "render(Lnet/minecraft/client/render/entity/state/ArmorStandEntityRenderState;Lnet/minecraft/client/util/math/MatrixStack;Lnet/minecraft/client/render/VertexConsumerProvider;I)V", at = @At("HEAD"), cancellable = true)
    private void Interact(ArmorStandEntityRenderState armorStandEntityRenderState, MatrixStack matrixStack, VertexConsumerProvider vertexConsumerProvider, int i, CallbackInfo ci) {
        if (NoRender.renderFilter.getList().contains("Armor Stand") && ModuleManager.getByName("NoRender").isEnabledRaw())  ci.cancel();
    }
}
