package ru.sandfoxy.horizen.modules.features.render;

import net.minecraft.client.MinecraftClient;
import ru.sandfoxy.horizen.mixin.utils.SimpleOptionAccessor;
import ru.sandfoxy.horizen.modules.core.Module;

public class Fullbright extends Module {
    private final MinecraftClient mc = MinecraftClient.getInstance();
    private double initial_value = 10.0;

    public Fullbright() {
        super("Fullbright", CATEGORY.RENDER, "Gamma fullbright");
    }


    @Override
    public void onEnable(){
        initial_value = mc.options.getGamma().getValue();

        ((SimpleOptionAccessor<Double>) (Object) mc.options.getGamma()).setValue(10.0);
    }

    @Override
    public void onDisable(){
        mc.options.getGamma().setValue(initial_value);
    }
}
