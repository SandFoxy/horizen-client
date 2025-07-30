package ru.sandfoxy.horizen.mixin.features.render;

import net.minecraft.block.enums.CameraSubmersionType;
import net.minecraft.client.render.BackgroundRenderer;
import net.minecraft.client.render.Camera;
import net.minecraft.client.render.Fog;
import net.minecraft.client.render.FogShape;
import org.joml.Vector4f;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;
import ru.sandfoxy.horizen.modules.ModuleManager;
import ru.sandfoxy.horizen.modules.features.render.FogCustom;
import com.mojang.blaze3d.systems.RenderSystem;

import java.awt.Color;

@Mixin(BackgroundRenderer.class)
public class BackgroundRendererMixin {

    @Inject(method = "applyFog", at = @At("TAIL"), cancellable = true)
    private static void onApplyFog(Camera camera, BackgroundRenderer.FogType fogType, Vector4f color, float viewDistance, boolean thickenFog, float tickDelta, CallbackInfoReturnable<Fog> cir) {
        if (ModuleManager.getByName("Custom Fog") != null && ModuleManager.getByName("Custom Fog").isEnabled() && 
            FogCustom.fogColor != null && FogCustom.fogDensity != null && FogCustom.fogDistance != null && 
            FogCustom.cylinderShape != null) {
            // Get custom fog settings  
            Color customFogColor = FogCustom.fogColor.getColor();
            float customDensity = FogCustom.fogDensity.getFloat();
            float customDistance = FogCustom.fogDistance.getFloat();
            boolean useCylinder = FogCustom.cylinderShape.getValue();
            
            // Apply custom fog color to the color vector
            color.x = customFogColor.getRed() / 255.0f;
            color.y = customFogColor.getGreen() / 255.0f;
            color.z = customFogColor.getBlue() / 255.0f;
            color.w = customFogColor.getAlpha() / 255.0f;
            
            // Calculate fog start and end based on custom settings
            // Используем более агрессивные настройки для полного покрытия
            float fogStart = Math.max(0.1f, customDistance * 0.1f / customDensity);
            float fogEnd = customDistance / Math.max(0.1f, customDensity);
            
            // Create and return modified fog with selected shape
            FogShape selectedShape = useCylinder ? FogShape.CYLINDER : FogShape.SPHERE;
            cir.setReturnValue(new Fog(fogStart, fogEnd, selectedShape,
                customFogColor.getRed() / 255.0f,
                customFogColor.getGreen() / 255.0f, 
                customFogColor.getBlue() / 255.0f, 
                customFogColor.getAlpha() / 255.0f));
        }
    }
} 