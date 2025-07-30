package ru.sandfoxy.horizen.mixin.features.player.viewmodel;

import net.minecraft.client.MinecraftClient;
import net.minecraft.client.network.AbstractClientPlayerEntity;
import net.minecraft.client.render.VertexConsumerProvider;
import net.minecraft.client.render.item.HeldItemRenderer;
import net.minecraft.client.util.math.MatrixStack;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.item.ItemStack;
import net.minecraft.item.consume.UseAction;
import net.minecraft.util.Arm;
import net.minecraft.util.Hand;
import net.minecraft.util.math.RotationAxis;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.ModifyArgs;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;
import org.spongepowered.asm.mixin.injection.invoke.arg.Args;
import ru.sandfoxy.horizen.modules.ModuleManager;
import ru.sandfoxy.horizen.modules.features.player.Viewmodel;

@Mixin(HeldItemRenderer.class)
public class HeldItemRendererMixin {
    @Inject(method = "renderFirstPersonItem", at = @At("HEAD"))
    public void onRenderHandsPos(AbstractClientPlayerEntity player, float tickDelta, float pitch, Hand hand, float swingProgress, ItemStack item, float equipProgress, MatrixStack m, VertexConsumerProvider vertexConsumers, int light, CallbackInfo ci) {
        if (!ModuleManager.getByName("View model").isEnabled()) return;

        m.push();

        if (hand == Hand.MAIN_HAND) {
            m.multiply(RotationAxis.POSITIVE_X.rotationDegrees(Viewmodel.mainRotationX.getFloat()));
            m.multiply(RotationAxis.POSITIVE_Y.rotationDegrees(Viewmodel.mainRotationY.getFloat()));
            m.multiply(RotationAxis.POSITIVE_Z.rotationDegrees(Viewmodel.mainRotationZ.getFloat()));
            m.translate(
                    Viewmodel.mainPositionX.getFloat(),
                    Viewmodel.mainPositionY.getFloat(),
                    Viewmodel.mainPositionZ.getFloat()
            );
        } else {
            m.multiply(RotationAxis.POSITIVE_X.rotationDegrees(Viewmodel.offRotationX.getFloat()));
            m.multiply(RotationAxis.POSITIVE_Y.rotationDegrees(Viewmodel.offRotationY.getFloat()));
            m.multiply(RotationAxis.POSITIVE_Z.rotationDegrees(Viewmodel.offRotationZ.getFloat()));
            m.translate(
                    Viewmodel.offPositionX.getFloat(),
                    Viewmodel.offPositionY.getFloat(),
                    Viewmodel.offPositionZ.getFloat()
            );
        }
    }

    @Inject(method = "renderFirstPersonItem", at = @At(value = "INVOKE", target = "Lnet/minecraft/client/render/item/HeldItemRenderer;renderItem(Lnet/minecraft/entity/LivingEntity;Lnet/minecraft/item/ItemStack;Lnet/minecraft/item/ModelTransformationMode;ZLnet/minecraft/client/util/math/MatrixStack;Lnet/minecraft/client/render/VertexConsumerProvider;I)V"))
    private void scaleItems(AbstractClientPlayerEntity player, float tickDelta, float pitch, Hand hand, float swingProgress, ItemStack item, float equipProgress, MatrixStack ms, VertexConsumerProvider vertexConsumers, int light, CallbackInfo ci) {
        if (!ModuleManager.getByName("View model").isEnabled()) return;

        float mainScale = Viewmodel.mainHandScale.getFloat();
        float offScale = Viewmodel.offHandScale.getFloat();
        if (hand == Hand.MAIN_HAND) {
            ms.scale(mainScale, mainScale, mainScale);
        } else {
            ms.scale(offScale, offScale, offScale);
        }
    }

    @Inject(method = "renderFirstPersonItem", at = @At(value = "INVOKE", target = "Lnet/minecraft/client/render/item/HeldItemRenderer;renderArmHoldingItem(Lnet/minecraft/client/util/math/MatrixStack;Lnet/minecraft/client/render/VertexConsumerProvider;IFFLnet/minecraft/util/Arm;)V"), cancellable = true)
    private void noHands(AbstractClientPlayerEntity player, float tickDelta, float pitch, Hand hand, float swingProgress, ItemStack item, float equipProgress, MatrixStack ms, VertexConsumerProvider vertexConsumers, int light, CallbackInfo ci) {
        if (!ModuleManager.getByName("View model").isEnabled()) return;
        if (Viewmodel.noHandRender.getValue()) ci.cancel();
    }

    @Inject(method = "applyEatOrDrinkTransformation", at = @At("HEAD"), cancellable = true)
    public void noEating(MatrixStack matrices, float tickDelta, Arm arm, ItemStack stack, PlayerEntity player, CallbackInfo ci) {
        if (!ModuleManager.getByName("View model").isEnabled()) return;
        if (Viewmodel.noFoodSwing.getValue() && MinecraftClient.getInstance().player.getActiveItem().getUseAction().equals(UseAction.EAT)) ci.cancel();
    }

    @Inject(method = "renderFirstPersonItem", at = @At("TAIL"))
    public void popMatrix(AbstractClientPlayerEntity player, float tickDelta, float pitch, Hand hand, float swingProgress, ItemStack item, float equipProgress, MatrixStack m, VertexConsumerProvider vertexConsumers, int light, CallbackInfo ci) {
        if (!ModuleManager.getByName("View model").isEnabled()) return;
        m.pop();
    }
}
