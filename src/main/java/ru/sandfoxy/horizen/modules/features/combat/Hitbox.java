package ru.sandfoxy.horizen.modules.features.combat;

import net.minecraft.client.MinecraftClient;
import net.minecraft.entity.Entity;
import net.minecraft.entity.LivingEntity;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.server.world.ServerWorld;
import net.minecraft.util.math.Box;
import net.minecraft.util.math.Vec3d;
import ru.sandfoxy.horizen.modules.core.Module;
import ru.sandfoxy.horizen.modules.core.type.Slider;


public class Hitbox extends Module {
    private final Slider sizeSlider = new Slider(1.f, 0.1f, 5.f, "Expand", Slider.SliderType.FLOAT);
    private static MinecraftClient mc = MinecraftClient.getInstance();

    public Hitbox() {
        super("Hitbox", CATEGORY.COMBAT, "Will expand hitbox of all entity's");
        this.addSetting(sizeSlider);
    }

    @Override
    public void endTick() {
        if (mc.world == null)
            return;

        for (Entity ent : mc.world.getPlayers()) {
            if (ent == null)
                return;

            if (ent instanceof PlayerEntity player) {
                if (player == mc.player) continue;

                Vec3d pos = player.getPos();
                player.setBoundingBox(new Box(pos.x - 0.3, pos.y, pos.z - 0.3, pos.x + 0.3, pos.y + 1.8, pos.z + 0.3).expand(sizeSlider.getFloat()));
            }
        }
    }

    @Override
    public void onDisable(){
        if (mc.world == null)
            return;

        for (Entity ent : mc.world.getPlayers()) {
            if (ent == null)
                return;

            if (ent instanceof PlayerEntity player) {
                Vec3d pos = player.getPos();
                player.setBoundingBox(new Box(pos.x - 0.3, pos.y, pos.z - 0.3, pos.x + 0.3, pos.y + 1.8, pos.z + 0.3));
            }
        }
    }
}
