package ru.sandfoxy.horizen.mixin.features.render.chams;

import com.mojang.blaze3d.systems.RenderSystem;
import net.minecraft.client.MinecraftClient;
import net.minecraft.client.render.VertexConsumerProvider;
import net.minecraft.client.render.entity.EntityRenderer;
import net.minecraft.client.render.entity.PlayerEntityRenderer;
import net.minecraft.client.render.entity.state.EntityRenderState;
import net.minecraft.client.render.entity.state.PlayerEntityRenderState;
import net.minecraft.client.util.math.MatrixStack;
import net.minecraft.entity.Entity;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.text.Text;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.LightType;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;
import ru.sandfoxy.horizen.modules.ModuleManager;
import ru.sandfoxy.horizen.modules.features.render.ESP;
import ru.sandfoxy.horizen.modules.features.render.EntityFullbright;

@Mixin(EntityRenderer.class)
public class EntityRendererMixin {
    @Inject(method = "getBlockLight", at = @At("RETURN"), cancellable = true)
    public <T extends Entity> void getLight(T entity, BlockPos pos, CallbackInfoReturnable<Integer> cir) {
        if (!ModuleManager.getByName("Entity Fullbright").isEnabled()) return;
        int glow = EntityFullbright.glowValue.getInt();

        if (glow < 0) {
            cir.setReturnValue(entity.isOnFire() ? 15 : entity.getWorld().getLightLevel(LightType.BLOCK, pos));
        } else {
            cir.setReturnValue(Math.min(glow, 15));
        }
    }
}
