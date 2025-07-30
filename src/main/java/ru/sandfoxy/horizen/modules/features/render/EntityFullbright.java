package ru.sandfoxy.horizen.modules.features.render;

import net.minecraft.client.MinecraftClient;
import ru.sandfoxy.horizen.mixin.utils.SimpleOptionAccessor;
import ru.sandfoxy.horizen.modules.core.Module;
import ru.sandfoxy.horizen.modules.core.type.Slider;

public class EntityFullbright extends Module {
    private final MinecraftClient mc = MinecraftClient.getInstance();
    public static Slider glowValue = new Slider(15,1,15, "Glow Value", Slider.SliderType.INT);

    public EntityFullbright() {
        super("Entity Fullbright", CATEGORY.RENDER, "Entity glow, basicly fullbright");
        this.addSetting(glowValue);
    }
}
