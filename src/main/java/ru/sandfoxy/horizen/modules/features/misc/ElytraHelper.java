package ru.sandfoxy.horizen.modules.features.misc;

import imgui.ImGui;
import imgui.flag.ImGuiWindowFlags;
import net.minecraft.client.MinecraftClient;
import net.minecraft.entity.Entity;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;
import net.minecraft.item.Items;
import net.minecraft.network.packet.c2s.play.PlayerInteractItemC2SPacket;
import net.minecraft.network.packet.c2s.play.UpdateSelectedSlotC2SPacket;
import net.minecraft.network.packet.c2s.play.ClickSlotC2SPacket;
import net.minecraft.screen.slot.SlotActionType;
import net.minecraft.util.Hand;
import net.minecraft.util.hit.EntityHitResult;
import net.minecraft.util.hit.HitResult;
import org.lwjgl.glfw.GLFW;
import ru.sandfoxy.horizen.modules.core.Module;
import ru.sandfoxy.horizen.modules.core.type.BindBox;
import ru.sandfoxy.horizen.modules.core.type.SaveableList;
import ru.sandfoxy.horizen.modules.features.hack.FriendList;
import it.unimi.dsi.fastutil.ints.Int2ObjectOpenHashMap;

import java.util.List;

public class ElytraHelper extends Module {
    private MinecraftClient mc = MinecraftClient.getInstance();
    public static ElytraHelper INSTANCE = null;
    private static final BindBox fireFirework = new BindBox("Firework");
    private static final BindBox elytraSwap = new BindBox("ElytraSwap");
    private static int fireworkDelay = 0;
    private static int swapDelay = 0;
    
    public ElytraHelper() {
        super("ElytraHelper", CATEGORY.MISC, "ElytraHelper.");

        this.addSetting(fireFirework);
        this.addSetting(elytraSwap);

        INSTANCE = this;
    }

    public List<Item> getHotbarItems(){
        List<Item> list = new java.util.ArrayList<>();
        if (mc.player == null) return list;

        for (int i = 0; i < 9; i++) {
            ItemStack stack = mc.player.getInventory().getStack(i);
            list.add(stack.getItem());
        }
        return list;
    }

    public int getFireworkSlot(){
        if (mc.player == null) return -1;
        
        for (int i = 0; i < 9; i++) {
            ItemStack stack = mc.player.getInventory().getStack(i);
            if (stack.getItem() == Items.FIREWORK_ROCKET) {
                return i;
            }
        }
        return -1;
    }

    public int getElytraSlot(){
        if (mc.player == null) return -1;
        
        for (int i = 0; i < 36; i++) {
            ItemStack stack = mc.player.getInventory().getStack(i);
            if (stack.getItem() == Items.ELYTRA) {
                return i;
            }
        }
        return -1;
    }

    public int getChestplateSlot(){
        if (mc.player == null) return -1;
        
        for (int i = 0; i < 36; i++) {
            ItemStack stack = mc.player.getInventory().getStack(i);
            if (stack.isEmpty()) continue;
            
            String itemName = stack.getItem().toString().toLowerCase();
            // Проверяем различные типы нагрудников
            if (itemName.contains("chestplate") || 
                itemName.contains("туника") || 
                itemName.contains("нагрудник") ||
                itemName.contains("leather_chestplate") ||
                itemName.contains("chainmail_chestplate") ||
                itemName.contains("iron_chestplate") ||
                itemName.contains("golden_chestplate") ||
                itemName.contains("diamond_chestplate") ||
                itemName.contains("netherite_chestplate")) {
                return i;
            }
        }
        return -1;
    }

    public void swapElytraChestplate(){
        if (mc.player == null) return;
        
        ItemStack chestSlot = mc.player.getInventory().getArmorStack(2);
        int elytraSlot = getElytraSlot();
        int chestplateSlot = getChestplateSlot();

        boolean hasElytraEquipped = chestSlot.getItem() == Items.ELYTRA;
        boolean hasArmorEquipped = !chestSlot.isEmpty() && chestSlot.getItem() != Items.ELYTRA;
        boolean chestSlotEmpty = chestSlot.isEmpty();
        
        if (hasElytraEquipped) {
            if (chestplateSlot != -1) {
                int guiSlot = chestplateSlot < 9 ? chestplateSlot + 36 : chestplateSlot + 9;
                performSwap(6, guiSlot);
            }
        } else {
            if (elytraSlot != -1) {
                int guiSlot = elytraSlot < 9 ? elytraSlot + 36 : elytraSlot + 9;
                performSwap(6, guiSlot);
            }
        }
    }
    
    private void performSwap(int armorSlot, int inventorySlot) {
        mc.getNetworkHandler().sendPacket(new ClickSlotC2SPacket(
            0,
            0,
            armorSlot,
            0,
            SlotActionType.PICKUP,
            ItemStack.EMPTY,
            new Int2ObjectOpenHashMap<>()
        ));

        mc.getNetworkHandler().sendPacket(new ClickSlotC2SPacket(
            0,
            0,
            inventorySlot,
            0,
            SlotActionType.PICKUP,
            ItemStack.EMPTY,
            new Int2ObjectOpenHashMap<>()
        ));

        mc.getNetworkHandler().sendPacket(new ClickSlotC2SPacket(
            0,
            0,
            armorSlot,
            0,
            SlotActionType.PICKUP,
            ItemStack.EMPTY,
            new Int2ObjectOpenHashMap<>()
        ));
    }

    public boolean hasFireworks(){
        return getFireworkSlot() != -1;
    }

    public void fireFirework(){
        if (mc.world == null || mc.player == null || !mc.isWindowFocused()) return;

        if (mc.player.getMainHandStack().getItem() != Items.FIREWORK_ROCKET){
            int fireworkSlot = getFireworkSlot();

            if (fireworkSlot != -1){
                int previousSlot = mc.player.getInventory().selectedSlot;

                mc.getNetworkHandler().sendPacket(new UpdateSelectedSlotC2SPacket(fireworkSlot));
                mc.player.getInventory().selectedSlot = fireworkSlot;

                mc.getNetworkHandler().sendPacket(new PlayerInteractItemC2SPacket(
                        Hand.MAIN_HAND,
                        0,
                        mc.player.getYaw(),
                        mc.player.getPitch()
                ));

                mc.getNetworkHandler().sendPacket(new UpdateSelectedSlotC2SPacket(previousSlot));
                mc.player.getInventory().selectedSlot = previousSlot;

                fireworkDelay = 10;
            }
        }else {
            mc.getNetworkHandler().sendPacket(new PlayerInteractItemC2SPacket(
                    Hand.MAIN_HAND,
                    0,
                    mc.player.getYaw(),
                    mc.player.getPitch()
            ));

            fireworkDelay = 10;
        }
    }

    @Override
    public void startTick() {
        if (fireworkDelay > 0) fireworkDelay--;
        if (swapDelay > 0) swapDelay--;

        if (mc.world == null || mc.player == null || !mc.isWindowFocused()) return;

        if (fireFirework.isBinded() && fireworkDelay <= 0 && fireFirework.isDown()){
            if (mc.player.getMainHandStack().getItem() != Items.FIREWORK_ROCKET){
                int fireworkSlot = getFireworkSlot();

                if (fireworkSlot != -1){
                    int previousSlot = mc.player.getInventory().selectedSlot;

                    mc.getNetworkHandler().sendPacket(new UpdateSelectedSlotC2SPacket(fireworkSlot));
                    mc.player.getInventory().selectedSlot = fireworkSlot;

                    mc.getNetworkHandler().sendPacket(new PlayerInteractItemC2SPacket(
                            Hand.MAIN_HAND,
                            0,
                            mc.player.getYaw(),
                            mc.player.getPitch()
                    ));

                    mc.getNetworkHandler().sendPacket(new UpdateSelectedSlotC2SPacket(previousSlot));
                    mc.player.getInventory().selectedSlot = previousSlot;

                    fireworkDelay = 10;
                }
            }else {
                mc.getNetworkHandler().sendPacket(new PlayerInteractItemC2SPacket(
                        Hand.MAIN_HAND,
                        0,
                        mc.player.getYaw(),
                        mc.player.getPitch()
                ));

                fireworkDelay = 10;
            }

        }

        if (elytraSwap.isBinded() && swapDelay <= 0 && elytraSwap.isDown()){
            if (!mc.player.isSprinting()) swapElytraChestplate();

            swapDelay = 5;
        }
    }


}
