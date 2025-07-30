package ru.sandfoxy.horizen.modules.features.render;

import net.minecraft.client.MinecraftClient;
import net.minecraft.entity.effect.StatusEffects;
import ru.sandfoxy.horizen.modules.core.Module;
import ru.sandfoxy.horizen.modules.core.type.MultiComboBox;

import java.util.List;

public class NoRender extends Module {
    private final MinecraftClient mc = MinecraftClient.getInstance();
    private double initialShakeValue = 75.0;

    public static final MultiComboBox renderFilter = new MultiComboBox("Disable", 
        List.of("In Wall Overlay", "Blindness", "Nausea"),
        List.of("Hurt Shake", "In Wall Overlay", "Armor Stand", "Fire", "Scoreboard", "Bossbar", "Water", "Lava", "Blindness", "Nausea"));

    public NoRender() {
        super("NoRender", CATEGORY.RENDER, "Disables some rendering features.\nSometimes require you to reload chunks (F3 + A)");
        this.addSetting(renderFilter);
    }

    @Override
    public void onEnable() {
        initialShakeValue = mc.options.getDamageTiltStrength().getValue();
    }

    @Override
    public void startTick() {
        if (renderFilter.getList().contains("Hurt Shake")) mc.options.getDamageTiltStrength().setValue(0.0);
        
        if (mc.player != null) {
            if (renderFilter.getList().contains("Blindness")) {
                mc.player.removeStatusEffect(StatusEffects.BLINDNESS);
            }
            if (renderFilter.getList().contains("Nausea")) {
                mc.player.removeStatusEffect(StatusEffects.NAUSEA);
            }
        }
    }

    @Override
    public void onDisable() {
        mc.options.getDamageTiltStrength().setValue(initialShakeValue);
    }
}
