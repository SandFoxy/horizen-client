package ru.sandfoxy.horizen.modules.features.player;

import net.minecraft.client.MinecraftClient;
import net.minecraft.item.Items;
import net.minecraft.network.packet.c2s.play.PlayerInteractItemC2SPacket;
import net.minecraft.util.Hand;
import ru.sandfoxy.horizen.modules.core.Module;
import ru.sandfoxy.horizen.modules.core.type.MultiComboBox;

import java.util.List;

public class NoDelay extends Module {
    private final MinecraftClient mc = MinecraftClient.getInstance();

    public static final MultiComboBox delayTypes = new MultiComboBox("Delay Types",
        List.of("Jump Delay"),
        List.of("Jump Delay", "Bottle o' Enchanting"));

    public NoDelay() {
        super("NoDelay", CATEGORY.PLAYER, "Removes various delays in the game", "NoDelay\0Delay\0Fast\0Speed");
        this.addSetting(delayTypes);
    }

    @Override
    public void startTick(){
        if (mc.options.useKey.isPressed() && delayTypes.getList().contains("Bottle o' Enchanting") && mc.player.getMainHandStack().getItem() == Items.EXPERIENCE_BOTTLE) {
            for (int i = 0; i < 8; i++) {
                mc.getNetworkHandler().sendPacket(new PlayerInteractItemC2SPacket(Hand.MAIN_HAND,i,mc.player.getYaw(),mc.player.getPitch()));
                mc.player.swingHand(Hand.MAIN_HAND);
            }
        }
    }
} 