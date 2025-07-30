package ru.sandfoxy.horizen.modules.features.render;

import net.minecraft.client.MinecraftClient;
import ru.sandfoxy.horizen.modules.core.Module;
import ru.sandfoxy.horizen.modules.core.type.Checkbox;
import ru.sandfoxy.horizen.modules.core.type.ColorPicker;
import ru.sandfoxy.horizen.modules.core.type.Slider;

import java.awt.Color;

public class FogCustom extends Module {
    private final MinecraftClient mc = MinecraftClient.getInstance();

    public static ColorPicker fogColor;
    public static Slider fogDensity;
    public static Slider fogDistance;
    public static Checkbox cylinderShape;

    public FogCustom() {
        super("Custom Fog", CATEGORY.RENDER, "Allow you to change fog color, density, distance");

        fogColor = addSetting(new ColorPicker("Fog Color", new Color(128, 128, 128, 128)));
        fogDensity = addSetting(new Slider(1.0f, 0.1f, 10.0f, "Fog Density", Slider.SliderType.FLOAT));
        fogDistance = addSetting(new Slider(128.0f, 16.0f, 512.0f, "Fog Distance", Slider.SliderType.FLOAT));
        cylinderShape = addSetting(new Checkbox(true, "Cylinder Shape"));
    }

    @Override
    public void onEnable() {
        super.onEnable();
    }

    @Override
    public void onDisable() {
        super.onDisable();
    }
} 