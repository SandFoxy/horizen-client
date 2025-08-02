package ru.sandfoxy.horizen.modules.features.misc;

import net.minecraft.client.MinecraftClient;
import net.minecraft.screen.GenericContainerScreenHandler;
import net.minecraft.screen.ScreenHandler;
import net.minecraft.screen.slot.SlotActionType;
import ru.sandfoxy.horizen.modules.core.Module;
import ru.sandfoxy.horizen.modules.core.type.Checkbox;
import ru.sandfoxy.horizen.modules.core.type.Slider;

public class ChestStealer extends Module {

    private final MinecraftClient mc = MinecraftClient.getInstance();
    private long lastStealTime = 0;
    private static final Checkbox autoClose = new Checkbox(false, "Auto Close");
    private static final Slider delay = new Slider(250, 0, 1000, "Delay", Slider.SliderType.INT);

    public ChestStealer() {
        super("ChestStealer",CATEGORY.MISC, "Automatically steals items from chests");
        this.addSetting(delay);
        this.addSetting(autoClose);
    }

    @Override
    public void startTick() {
        if (!this.isEnabled()) return;

        if (mc.player == null || mc.currentScreen == null) return;
        if (!(mc.player.currentScreenHandler instanceof GenericContainerScreenHandler container)) return;

        if (System.currentTimeMillis() - lastStealTime < delay.getInt()) return;

        if (container.getInventory().isEmpty() && autoClose.getValue()) {
            mc.player.closeHandledScreen();
        }

        for (int i = 0; i < container.getInventory().size(); i++) {
            if (!container.getInventory().getStack(i).isEmpty()) {
                mc.interactionManager.clickSlot(
                        container.syncId,
                        i,
                        0,
                        SlotActionType.QUICK_MOVE,
                        mc.player
                );
                lastStealTime = System.currentTimeMillis();
                return;
            }
        }
    }
}
