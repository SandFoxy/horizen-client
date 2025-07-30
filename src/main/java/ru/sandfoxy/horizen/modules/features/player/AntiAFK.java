package ru.sandfoxy.horizen.modules.features.player;

import net.minecraft.client.MinecraftClient;
import ru.sandfoxy.horizen.modules.core.Module;

public class AntiAFK extends Module {
    private final MinecraftClient mc = MinecraftClient.getInstance();

    public AntiAFK() {
        super("AntiAFK", CATEGORY.PLAYER, "Will jump for you forever.", "NoKick\0AutoJump\0Jump");
    }


    @Override
    public void startTick(){
        if (mc.player == null) return;

        mc.options.jumpKey.setPressed(true);
    }

    @Override
    public void onDisable(){
        if (mc.player == null) return;

        mc.options.jumpKey.setPressed(false);
    }

}
