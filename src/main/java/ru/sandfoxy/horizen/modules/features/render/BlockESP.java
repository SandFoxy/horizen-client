package ru.sandfoxy.horizen.modules.features.render;

import net.minecraft.block.*;
import net.minecraft.client.MinecraftClient;
import net.minecraft.util.math.BlockPos;
import ru.sandfoxy.horizen.modules.core.Module;
import ru.sandfoxy.horizen.modules.core.type.MultiComboBox;

import java.awt.*;
import java.util.*;
import java.util.List;

import static ru.sandfoxy.horizen.utils.render.RenderUtils.ConvertColor;
import static ru.sandfoxy.horizen.utils.render.RenderUtils.drawBlock;

public class BlockESP extends Module {
    private static MinecraftClient mc = MinecraftClient.getInstance();
    private static final int MAX_RADIUS = 20;
    private static final int UPDATE_INTERVAL = 30;
    private int tickCounter = 0;
    
    private final MultiComboBox blocksToGlow = new MultiComboBox("Blocks", List.of("Storages", "Valuables", "Spawners"), List.of("Storages", "Valuables", "Spawners"));
    
    private final Map<BlockPos, Color> cachedBlocks = new HashMap<>();
    private BlockPos lastPlayerPos = null;

    public static final Map<Block, Color> VALUABLES = Map.of(
            Blocks.DIAMOND_ORE, new Color(0, 255, 255),
            Blocks.EMERALD_ORE, new Color(0, 255, 0),
            Blocks.ANCIENT_DEBRIS, new Color(139, 69, 19),
            Blocks.DIAMOND_BLOCK, new Color(0, 255, 255),
            Blocks.EMERALD_BLOCK, new Color(0, 255, 0),
            Blocks.GOLD_BLOCK, new Color(255, 215, 0),
            Blocks.NETHERITE_BLOCK, new Color(105, 105, 105)
    );

    public BlockESP() {
        super("BlockESP", CATEGORY.RENDER, "Will glow up useful blocks.");
        this.addSetting(blocksToGlow);
    }

    private void updateCache() {
        if (mc.player == null || mc.world == null) return;
        
        BlockPos playerPos = mc.player.getBlockPos();
        if (lastPlayerPos != null && lastPlayerPos.equals(playerPos) && tickCounter < UPDATE_INTERVAL) {
            tickCounter++;
            return;
        }

        cachedBlocks.clear();
        lastPlayerPos = playerPos;
        tickCounter = 0;

        int radius = Math.min(MAX_RADIUS, mc.options.getViewDistance().getValue() * 16);
        int radiusSquared = radius * radius;

        for (int y = -radius; y <= radius; y++) {
            for (int x = -radius; x <= radius; x++) {
                for (int z = -radius; z <= radius; z++) {
                    BlockPos pos = playerPos.add(x, y, z);
                    if (pos.getSquaredDistance(playerPos) > radiusSquared) continue;
                    
                    BlockState state = mc.world.getBlockState(pos);
                    Block blockType = state.getBlock();

                    if (blocksToGlow.getList().contains("Storages")) {
                        if (blockType instanceof ChestBlock) {
                            cachedBlocks.put(pos, Color.ORANGE);
                        } else if (blockType instanceof EnderChestBlock) {
                            cachedBlocks.put(pos, Color.BLUE);
                        } else if (blockType instanceof ShulkerBoxBlock shulkerbox) {
                            cachedBlocks.put(pos, shulkerbox.getColor() != null ? 
                                new Color(shulkerbox.getColor().getEntityColor()) : Color.PINK);
                        }
                    }
                    
                    if (blocksToGlow.getList().contains("Valuables") && VALUABLES.containsKey(blockType)) {
                        cachedBlocks.put(pos, VALUABLES.get(blockType));
                    }
                    
                    if (blocksToGlow.getList().contains("Spawners") && blockType instanceof SpawnerBlock) {
                        cachedBlocks.put(pos, Color.BLACK);
                    }
                }
            }
        }
    }

    @Override
    public void onDraw() {
        if (mc.player == null || mc.world == null) return;

        updateCache();
        
        for (Map.Entry<BlockPos, Color> entry : cachedBlocks.entrySet()) {
            drawBlock(entry.getKey(), ConvertColor(entry.getValue()));
        }
    }
}

