package ru.sandfoxy.horizen.mixin;

import net.minecraft.client.gui.DrawContext;
import net.minecraft.client.gui.hud.InGameHud;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.util.Identifier;
import net.minecraft.util.math.random.Random;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;
import ru.sandfoxy.horizen.modules.ModuleManager;
import ru.sandfoxy.horizen.utils.render.RenderUtils;

@Mixin(InGameHud.class)
public class ShowSaturationMixin {
    @Shadow
    private int ticks;

    @Shadow
    private final Random random = Random.create();

    @Shadow
    private static final Identifier FOOD_EMPTY_HUNGER_TEXTURE = Identifier.ofVanilla("hud/food_empty_hunger");

    @Shadow
    private static final Identifier FOOD_HALF_HUNGER_TEXTURE = Identifier.ofVanilla("hud/food_half_hunger");

    @Shadow
    private static final Identifier FOOD_FULL_HUNGER_TEXTURE = Identifier.ofVanilla("hud/food_full_hunger");

    @Shadow
    private static final Identifier FOOD_EMPTY_TEXTURE = Identifier.ofVanilla("hud/food_empty");

    @Shadow
    private static final Identifier FOOD_HALF_TEXTURE = Identifier.ofVanilla("hud/food_half");

    @Shadow
    private static final Identifier FOOD_FULL_TEXTURE = Identifier.ofVanilla("hud/food_full");

    @Inject(
            method = "renderFood",
            at = @At("HEAD"),
            cancellable = true
    )
    public void renderFood(DrawContext context, PlayerEntity player, int top, int right, CallbackInfo ci) {
        if (!ModuleManager.getByName("Show Saturation").isEnabledRaw()) return;

        RenderUtils.renderFood(FOOD_EMPTY_HUNGER_TEXTURE, FOOD_HALF_TEXTURE, FOOD_FULL_TEXTURE, FOOD_EMPTY_TEXTURE, FOOD_HALF_TEXTURE, FOOD_FULL_TEXTURE, random, ticks, context, player, top, right);
        ci.cancel();
    }

}
