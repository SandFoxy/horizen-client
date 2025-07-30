package ru.sandfoxy.horizen.mixin.features.render.ambient;

import net.minecraft.world.biome.Biome;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;

import ru.sandfoxy.horizen.modules.ModuleManager;
import ru.sandfoxy.horizen.modules.features.render.Ambience;

import java.util.Objects;

@Mixin(Biome.class)
public class BiomeMixin {
    @Inject(method = "getPrecipitation", at = @At("HEAD"), cancellable = true)
    private void getPrecipitation(CallbackInfoReturnable<Biome.Precipitation> cir) {
        if (ModuleManager.getByName("Ambience").isEnabledRaw() && Objects.equals(Ambience.weather.getValue(), "Snow")) {
            cir.setReturnValue(Biome.Precipitation.SNOW);
        }
    }
} 