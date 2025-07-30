package ru.sandfoxy.horizen.modules.features.misc;

import net.minecraft.client.MinecraftClient;
import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;
import net.minecraft.item.Items;
import net.minecraft.network.packet.c2s.play.PlayerInteractItemC2SPacket;
import net.minecraft.network.packet.c2s.play.UpdateSelectedSlotC2SPacket;
import net.minecraft.util.Hand;
import ru.sandfoxy.horizen.modules.core.Module;
import ru.sandfoxy.horizen.modules.core.type.BindBox;
import ru.sandfoxy.horizen.modules.core.type.Checkbox;

import java.util.List;

public class MaceHelper extends Module {
    private MinecraftClient mc = MinecraftClient.getInstance();
    private static final BindBox useWindChargeKey = new BindBox("Use Wind Charge");
    private static final Checkbox onlyGround = new Checkbox(true,"Only On Ground");
    private static int windDelay = 0;

    public MaceHelper() {
        super("MaceHelper", CATEGORY.MISC, "MaceHelper");

        this.addSetting(useWindChargeKey);
        this.addSetting(onlyGround);
    }

    public int getWindChargeSlot(){
        if (mc.player == null) return -1;

        for (int i = 0; i < 9; i++) {
            ItemStack stack = mc.player.getInventory().getStack(i);
            if (stack.getItem() == Items.WIND_CHARGE) {
                return i;
            }
        }
        return -1;
    }


    @Override
    public void startTick() {
        if (windDelay > 0) windDelay--;

        if (mc.world == null || mc.player == null || !mc.isWindowFocused()) return;

        if (onlyGround.getValue() && !mc.player.isOnGround()) return;

        if (useWindChargeKey.isBinded() && windDelay <= 0 && useWindChargeKey.isDown()){
            if (mc.player.getMainHandStack().getItem() != Items.WIND_CHARGE){
                int windchargeSlot = getWindChargeSlot();

                if (windchargeSlot != -1){
                    int previousSlot = mc.player.getInventory().selectedSlot;

                    mc.getNetworkHandler().sendPacket(new UpdateSelectedSlotC2SPacket(windchargeSlot));
                    mc.player.getInventory().selectedSlot = windchargeSlot;

                    mc.getNetworkHandler().sendPacket(new PlayerInteractItemC2SPacket(
                            Hand.MAIN_HAND,
                            0,
                            -90,
                            90
                    ));

                    mc.getNetworkHandler().sendPacket(new UpdateSelectedSlotC2SPacket(previousSlot));
                    mc.player.getInventory().selectedSlot = previousSlot;

                    windDelay = 10;
                }
            }else {
                mc.getNetworkHandler().sendPacket(new PlayerInteractItemC2SPacket(
                        Hand.MAIN_HAND,
                        0,
                        -90,
                        90
                ));

                windDelay = 10;
            }

        }
    }


}
