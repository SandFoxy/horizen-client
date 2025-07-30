package ru.sandfoxy.horizen.mixin.features.render.norender;

import net.minecraft.block.BlockState;
import net.minecraft.client.render.VertexConsumer;
import net.minecraft.client.render.block.FluidRenderer;
import net.minecraft.fluid.FluidState;
import net.minecraft.registry.tag.FluidTags;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.BlockRenderView;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;
import ru.sandfoxy.horizen.modules.ModuleManager;
import ru.sandfoxy.horizen.modules.features.render.NoRender;

@Mixin(FluidRenderer.class)
public class FluidRendererMixin {

    @Inject(method = "render", at = @At("HEAD"), cancellable = true)
    private void cancelFluidRendering(BlockRenderView world, BlockPos pos, VertexConsumer vertexConsumer, BlockState blockState, FluidState fluidState, CallbackInfo ci) {
        if (!ModuleManager.getByName("NoRender").isEnabledRaw()) return;

        if (NoRender.renderFilter.getList().contains("Water") && fluidState.isIn(FluidTags.WATER))  ci.cancel();
        if (NoRender.renderFilter.getList().contains("Lava") && fluidState.isIn(FluidTags.LAVA))  ci.cancel();
    }
}