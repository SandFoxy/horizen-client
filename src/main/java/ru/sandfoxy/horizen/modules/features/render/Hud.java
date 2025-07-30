package ru.sandfoxy.horizen.modules.features.render;

import net.minecraft.client.MinecraftClient;
import ru.sandfoxy.horizen.modules.core.Module;

public class Hud extends Module {
    private final MinecraftClient mc = MinecraftClient.getInstance();

    //private static final Checkbox musicPlayer = new Checkbox(false, "Music Player");

    public Hud() {
        super("Hud", CATEGORY.RENDER, "Hud Settings");

        //this.addSetting(musicPlayer);
    }


    @Override
    public void onGui(){

    }
}
