package ru.sandfoxy.horizen.modules.features.combat;

import net.minecraft.client.MinecraftClient;
import ru.sandfoxy.horizen.modules.core.Module;
import ru.sandfoxy.horizen.modules.core.type.ComboBox;
import net.minecraft.item.ItemStack;
import ru.sandfoxy.horizen.modules.ServerInfo;

import java.util.List;
import java.util.HashSet;
import java.util.Set;

public class LeaveOnLoot extends Module {
    private MinecraftClient mc = MinecraftClient.getInstance();
    private final ComboBox whereToLeave = new ComboBox("Where to leave?","/hub", List.of("/hub", "/spawn", "/home"));
    private Set<ItemStack> previousInventory = new HashSet<>();
    private final Set<String> valuableItems = Set.of(
        "DIAMOND",
        "NETHERITE",
        "BEACON",
        "ELYTRA",
        "TRIDENT"
    );

    public LeaveOnLoot() {
        super("LeaveOnLoot", CATEGORY.COMBAT, "Automatically leaves when you loot valuable items");
        this.addSetting(whereToLeave);
    }

    @Override
    public void onEnable(){
        if (mc.world == null || mc.player == null || mc.crosshairTarget == null || !mc.isWindowFocused()) return;

        Set<ItemStack> currentInventory = new HashSet<>();
        for (int i = 0; i < mc.player.getInventory().size(); i++) {
            ItemStack stack = mc.player.getInventory().getStack(i);
            if (!stack.isEmpty()) {
                currentInventory.add(stack);
            }
        }

        previousInventory = currentInventory;
    }

    @Override
    public void startTick() {
        if (mc.world == null || mc.player == null || mc.crosshairTarget == null || !mc.isWindowFocused()) return;

        Set<ItemStack> currentInventory = new HashSet<>();
        for (int i = 0; i < mc.player.getInventory().size(); i++) {
            ItemStack stack = mc.player.getInventory().getStack(i);
            if (!stack.isEmpty()) {
                currentInventory.add(stack);
            }
        }

        if (ServerInfo.isInPvP()) return;

        for (ItemStack stack : currentInventory) {
            if (!previousInventory.contains(stack)) {
                String itemName = stack.getItem().toString().toUpperCase();
                String displayName = stack.getName().getString().toUpperCase();
                
                if (itemName.contains("DIAMOND") && displayName.toLowerCase().contains("изумрудный меч")) {
                    continue;
                }
                
                if (valuableItems.stream().anyMatch(itemName::contains)) {
                    String command = whereToLeave.getValue();
                    mc.player.networkHandler.sendChatCommand(command.substring(1));
                    break;
                }
            }
        }

        previousInventory = currentInventory;
    }
}
