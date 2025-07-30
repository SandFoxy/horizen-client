package ru.sandfoxy.horizen.modules.features.player;

import net.minecraft.client.MinecraftClient;
import ru.sandfoxy.horizen.modules.core.Module;

public class ShowSaturation extends Module {
    private final MinecraftClient mc = MinecraftClient.getInstance();

    public ShowSaturation() {
        super("Show Saturation", CATEGORY.PLAYER, "Visualize your saturation by adding more food.", "Saturation\0Food\0Jump", true);
    }
}
