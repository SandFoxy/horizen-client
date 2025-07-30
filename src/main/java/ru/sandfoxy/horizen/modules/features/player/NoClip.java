package ru.sandfoxy.horizen.modules.features.player;

import net.minecraft.client.MinecraftClient;
import net.minecraft.item.Items;
import net.minecraft.network.packet.c2s.play.PlayerInteractItemC2SPacket;
import net.minecraft.util.Hand;
import ru.sandfoxy.horizen.modules.core.Module;
import ru.sandfoxy.horizen.modules.core.type.MultiComboBox;

import java.util.List;

public class NoClip extends Module {
    private final MinecraftClient mc = MinecraftClient.getInstance();

    public NoClip() {
        super("NoClip", CATEGORY.PLAYER, "If enabled, you won't be pushed out of blocks");
    }
}