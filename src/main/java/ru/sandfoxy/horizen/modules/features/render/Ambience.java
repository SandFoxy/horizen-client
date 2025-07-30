package ru.sandfoxy.horizen.modules.features.render;

import net.minecraft.client.MinecraftClient;
import net.minecraft.client.world.ClientWorld;
import net.minecraft.network.packet.s2c.play.WorldTimeUpdateS2CPacket;
import ru.sandfoxy.horizen.modules.core.Module;
import ru.sandfoxy.horizen.modules.core.type.ComboBox;
import ru.sandfoxy.horizen.modules.core.type.Slider;

import java.util.List;
import java.util.Random;

public class Ambience extends Module {
    public static Ambience INSTANCE;
    private final MinecraftClient mc = MinecraftClient.getInstance();
    private final Slider time = new Slider(12000, 0, 24000, "Time", Slider.SliderType.INT);
    public static final ComboBox weather = new ComboBox("Weather", "Clear", List.of("Clear", "Rain", "Thunder", "Snow", "Don't Change"));
    private final int randomValue = (300 + (new Random()).nextInt(600)) * 20;

    public Ambience() {
        super("Ambience", CATEGORY.RENDER, "Customize world time and weather");
        this.addSetting(time);
        this.addSetting(weather);
        INSTANCE = this;
    }

    @Override
    public void startTick() {
        if (mc.world != null) {
            ClientWorld world = mc.world;
            world.getLevelProperties().setTimeOfDay(time.getInt());

            switch (weather.getValue()) {
                case "Clear":
                    world.getLevelProperties().setRaining(false);
                    world.setRainGradient(0.0f);
                    world.setThunderGradient(0.0f);
                    break;
                case "Rain":
                    world.getLevelProperties().setRaining(true);
                    world.setRainGradient(1.0f);
                    world.setThunderGradient(0.0f);
                    break;
                case "Thunder":
                    world.getLevelProperties().setRaining(true);
                    world.setRainGradient(1.0f);
                    world.setThunderGradient(1.0f);
                    break;
                case "Snow":
                    world.getLevelProperties().setRaining(true);
                    world.setRainGradient(1.0f);
                    world.setThunderGradient(0.0f);
                    break;
            }
        }
    }

    @Override
    public void onDisable() {
        super.onDisable();
    }
} 