package ru.sandfoxy.horizen.mixin;

import net.minecraft.client.gui.DrawContext;
import net.minecraft.client.gui.screen.TitleScreen;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;
import ru.sandfoxy.horizen.utils.render.RenderUtils;

import static ru.sandfoxy.horizen.modules.features.misc.Unhook.cheatUnloaded;

@Mixin(value = TitleScreen.class)
public class TitleScreenMixin {

    @Inject(method = "render", at = @At("TAIL"))
    public void render(DrawContext context, int mouseX, int mouseY, float delta, CallbackInfo ci) {
        if (!cheatUnloaded) RenderUtils.titlescreenRender(context, delta);
    }
}
