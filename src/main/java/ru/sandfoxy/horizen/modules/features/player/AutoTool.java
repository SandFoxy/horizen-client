package ru.sandfoxy.horizen.modules.features.player;

import net.minecraft.block.Block;
import net.minecraft.block.BlockState;
import net.minecraft.client.MinecraftClient;
import net.minecraft.item.ItemStack;
import net.minecraft.util.hit.BlockHitResult;
import net.minecraft.util.hit.HitResult;
import ru.sandfoxy.horizen.modules.core.Module;
import ru.sandfoxy.horizen.modules.core.type.Checkbox;

public class AutoTool extends Module {
    private final MinecraftClient mc = MinecraftClient.getInstance();
    private final Checkbox switchBack = new Checkbox(true, "Switch Back");
    private int previousSlot = -1;

    public AutoTool() {
        super("AutoTool", CATEGORY.PLAYER, "Automatically selects the best tool for breaking blocks", "ToolSwitch\0ToolSelect\0AutoSwitch");
        this.addSetting(switchBack);
    }

    @Override
    public void startTick() {
        if (mc.world == null || mc.player == null) return;

        if (mc.crosshairTarget == null || mc.crosshairTarget.getType() != HitResult.Type.BLOCK) {
            if (switchBack.getValue() && previousSlot != -1) {
                mc.player.getInventory().selectedSlot = previousSlot;
                previousSlot = -1;
            }
            return;
        }

        BlockHitResult hit = (BlockHitResult) mc.crosshairTarget;
        BlockState state = mc.world.getBlockState(hit.getBlockPos());
        Block block = state.getBlock();

        if (block.getHardness() <= 0 || !mc.options.attackKey.isPressed()) return;

        float bestSpeed = 1.0f;
        int bestSlot = -1;

        for (int i = 0; i < 9; i++) {
            ItemStack stack = mc.player.getInventory().getStack(i);
            if (stack.isEmpty()) continue;

            float speed = stack.getMiningSpeedMultiplier(state);
            if (speed > bestSpeed) {
                bestSpeed = speed;
                bestSlot = i;
            }
        }

        if (bestSlot != -1 && bestSlot != mc.player.getInventory().selectedSlot) {
            if (previousSlot == -1) {
                previousSlot = mc.player.getInventory().selectedSlot;
            }
            mc.player.getInventory().selectedSlot = bestSlot;
        }
    }

    @Override
    public void onDisable() {
        if (mc.player != null && switchBack.getValue() && previousSlot != -1) {
            mc.player.getInventory().selectedSlot = previousSlot;
            previousSlot = -1;
        }
    }
} 