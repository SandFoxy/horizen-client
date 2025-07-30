package ru.sandfoxy.horizen.modules.features.render;

import net.minecraft.client.MinecraftClient;
import net.minecraft.entity.Entity;
import net.minecraft.entity.ItemEntity;
import net.minecraft.item.*;
import net.minecraft.util.math.MathHelper;
import net.minecraft.util.math.Vec3d;
import ru.sandfoxy.horizen.imgui.ImGuiUils;
import ru.sandfoxy.horizen.modules.core.Module;
import ru.sandfoxy.horizen.modules.core.type.Checkbox;
import ru.sandfoxy.horizen.modules.core.type.ColorPicker;
import ru.sandfoxy.horizen.modules.core.type.MultiComboBox;
import ru.sandfoxy.horizen.utils.math.WorldToScreen;

import java.awt.*;
import java.util.List;

import static ru.sandfoxy.horizen.utils.render.RenderUtils.ConvertColor;

public class ItemESP extends Module {
    private static MinecraftClient mc = MinecraftClient.getInstance();

    public static final MultiComboBox itemsFilter = new MultiComboBox("Filter", List.of("Armor", "Weapon", "Potions", "Totems"),List.of("Armor", "Weapon", "Potions", "Totems","Others"));
    public static final Checkbox itemGlow = new Checkbox(true, "Glow");
    public static final ColorPicker glowColor = new ColorPicker("Glow Color", Color.CYAN);

    public ItemESP() {
        super("ItemESP", CATEGORY.RENDER, "Will render all dropped items.");
        this.addSetting(itemsFilter);
        this.addSetting(itemGlow);
        this.addSetting(glowColor);

        glowColor.visibleIf(itemGlow::get);
    }
    public static  Vec3d interpolate(Entity entity, float tickDelta) {
        double x = MathHelper.lerp(tickDelta, entity.lastRenderX, entity.getX());
        double y = MathHelper.lerp(tickDelta, entity.lastRenderY, entity.getY());
        double z = MathHelper.lerp(tickDelta, entity.lastRenderZ, entity.getZ());
        return new Vec3d(x, y + entity.getStandingEyeHeight(), z);
    }

    @Override
    public void onDraw() {
        if (mc.player == null || mc.world == null) return;

        List<String> activeFilters = itemsFilter.getList();

        for (Entity ent : mc.world.getEntities()) {
            if (ent instanceof ItemEntity item) {
                ItemStack stack = item.getStack();
                Item i = stack.getItem();

                String category = getItemCategory(i);

                if (!activeFilters.contains(category)) continue;

                Vec3d position = interpolate(ent, WorldToScreen.tickDelta).add(0, 0.1f, 0);
                Vec3d onScreen = WorldToScreen.w2s(position);

                if (onScreen == null) continue;

                ImGuiUils.drawTextCentred(
                        item.getName().getString() + (stack.getCount() > 1 ? " x" + stack.getCount() : ""),
                        (float) onScreen.x, (float) onScreen.y,
                        12.f,ConvertColor(Color.white), true
                );
            }
        }
    }

    public static String getItemCategory(Item item) {
        if (item instanceof ArmorItem) return "Armor";
        if (item instanceof SwordItem || item instanceof BowItem || item instanceof EndCrystalItem || item instanceof CrossbowItem || item instanceof MaceItem || item instanceof TridentItem) return "Weapon";
        if (item instanceof PotionItem) return "Potions";
        if (item == Items.TOTEM_OF_UNDYING) return "Totems";
        return "Others";
    }
}
