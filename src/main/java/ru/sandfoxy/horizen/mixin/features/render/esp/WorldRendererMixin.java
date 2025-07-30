package ru.sandfoxy.horizen.mixin.features.render.esp;

import net.minecraft.client.render.OutlineVertexConsumerProvider;
import net.minecraft.client.render.VertexConsumerProvider;
import net.minecraft.client.render.WorldRenderer;
import net.minecraft.client.util.math.MatrixStack;
import net.minecraft.entity.Entity;
import net.minecraft.entity.ItemEntity;
import net.minecraft.entity.player.PlayerEntity;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;
import ru.sandfoxy.horizen.modules.ModuleManager;
import ru.sandfoxy.horizen.modules.features.hack.FriendList;
import ru.sandfoxy.horizen.modules.features.render.ESP;
import ru.sandfoxy.horizen.modules.features.render.ItemESP;

import java.awt.*;

import static org.lwjgl.opengl.GL11C.*;

@Mixin(WorldRenderer.class)
public abstract class WorldRendererMixin {

    @Inject(method = "renderEntity", at = @At("HEAD"))
    private void renderEntity(Entity entity, double cameraX, double cameraY, double cameraZ, float tickDelta, MatrixStack matrices, VertexConsumerProvider vertexConsumers, CallbackInfo ci) {
        glDisable(GL_DEPTH_TEST);
        if (ESP.playerGlow.getValue() && ModuleManager.getByName("ESP").isEnabled() && vertexConsumers instanceof OutlineVertexConsumerProvider outlineVertexConsumers && entity instanceof PlayerEntity player) {
            Color color = FriendList.isFriend(player) ? Color.green : ESP.glowColor.getColor();
            outlineVertexConsumers.setColor(color.getRed(), color.getGreen(), color.getBlue(), color.getAlpha());
        }
        if (ESP.playerGlow.getValue() && ModuleManager.getByName("ItemESP").isEnabled() && vertexConsumers instanceof OutlineVertexConsumerProvider outlineVertexConsumers && entity instanceof ItemEntity) {
            Color color = ItemESP.glowColor.getColor();
            outlineVertexConsumers.setColor(color.getRed(), color.getGreen(), color.getBlue(), color.getAlpha());
        }
    }

    @Inject(method = "renderEntity", at = @At("TAIL"))
    private void renderEntityTail(Entity entity, double cameraX, double cameraY, double cameraZ, float tickDelta, MatrixStack matrices, VertexConsumerProvider vertexConsumers, CallbackInfo ci) {
        glEnable(GL_DEPTH_TEST);
    }
}