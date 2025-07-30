package ru.sandfoxy.horizen.modules.features.misc;

import net.minecraft.block.RespawnAnchorBlock;
import net.minecraft.client.MinecraftClient;
import net.minecraft.item.Items;
import net.minecraft.util.Hand;
import net.minecraft.util.hit.BlockHitResult;
import net.minecraft.util.hit.HitResult;
import ru.sandfoxy.horizen.modules.core.Module;

public class FastAnchor extends Module {
    private final MinecraftClient mc = MinecraftClient.getInstance();

    public FastAnchor() {
        super("FastAnchor", CATEGORY.MISC, "FastAnchor");
    }

    @Override
    public void startTick() {
        if (mc.world == null || mc.player == null || !mc.isWindowFocused()) return;

        if (mc.player.getMainHandStack().getItem() != Items.GLOWSTONE &&
            mc.player.getOffHandStack().getItem() != Items.GLOWSTONE) {
            return;
        }

        if (mc.crosshairTarget != null && mc.crosshairTarget.getType() == HitResult.Type.BLOCK) {
            BlockHitResult blockHit = (BlockHitResult) mc.crosshairTarget;

            if (mc.world.getBlockState(blockHit.getBlockPos()).getBlock() instanceof RespawnAnchorBlock anchor && mc.options.useKey.isPressed()) {
                if (mc.world.getBlockState(blockHit.getBlockPos()).get(RespawnAnchorBlock.CHARGES) < 4) mc.interactionManager.interactBlock(mc.player, Hand.MAIN_HAND, blockHit);
            }
        }
    }

}
