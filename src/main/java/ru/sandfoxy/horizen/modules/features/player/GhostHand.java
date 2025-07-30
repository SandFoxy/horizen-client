package ru.sandfoxy.horizen.modules.features.player;

import net.minecraft.block.Block;
import net.minecraft.block.BlockState;
import net.minecraft.client.MinecraftClient;
import net.minecraft.util.math.BlockPos;
import ru.sandfoxy.horizen.modules.core.Module;
import ru.sandfoxy.horizen.modules.core.type.MultiComboBox;

import java.util.List;

public class GhostHand extends Module {
    private static final MinecraftClient mc = MinecraftClient.getInstance();
    private static final MultiComboBox blocks = new MultiComboBox("Blocks",
        List.of("minecraft:barrel", "minecraft:black_shulker_box", "minecraft:blue_shulker_box",
        "minecraft:brown_shulker_box", "minecraft:chest", "minecraft:cyan_shulker_box",
        "minecraft:dispenser", "minecraft:dropper", "minecraft:ender_chest",
        "minecraft:gray_shulker_box", "minecraft:green_shulker_box", "minecraft:hopper",
        "minecraft:light_blue_shulker_box", "minecraft:light_gray_shulker_box",
        "minecraft:lime_shulker_box", "minecraft:magenta_shulker_box",
        "minecraft:orange_shulker_box", "minecraft:pink_shulker_box",
        "minecraft:purple_shulker_box", "minecraft:red_shulker_box",
        "minecraft:shulker_box", "minecraft:trapped_chest",
        "minecraft:white_shulker_box", "minecraft:yellow_shulker_box"),
        List.of("minecraft:barrel", "minecraft:black_shulker_box", "minecraft:blue_shulker_box",
        "minecraft:brown_shulker_box", "minecraft:chest", "minecraft:cyan_shulker_box",
        "minecraft:dispenser", "minecraft:dropper", "minecraft:ender_chest",
        "minecraft:gray_shulker_box", "minecraft:green_shulker_box", "minecraft:hopper",
        "minecraft:light_blue_shulker_box", "minecraft:light_gray_shulker_box",
        "minecraft:lime_shulker_box", "minecraft:magenta_shulker_box",
        "minecraft:orange_shulker_box", "minecraft:pink_shulker_box",
        "minecraft:purple_shulker_box", "minecraft:red_shulker_box",
        "minecraft:shulker_box", "minecraft:trapped_chest",
        "minecraft:white_shulker_box", "minecraft:yellow_shulker_box"));

    public GhostHand() {
        super("GhostHand", CATEGORY.PLAYER, "Allows you to interact with blocks trough walls.", "Interact", true);
        this.addSetting(blocks);
    }

    public static boolean isBlockInList(BlockPos pos) {
        if (mc.world == null) return false;
        BlockState state = mc.world.getBlockState(pos);
        Block block = state.getBlock();
        String blockName = block.getTranslationKey().replace("block.minecraft.", "minecraft:");
        return blocks.getList().contains(blockName);
    }
}
