package ru.sandfoxy.horizen.mixin.features.render.norender;

import net.minecraft.client.gui.DrawContext;
import net.minecraft.client.gui.hud.BossBarHud;
import net.minecraft.client.gui.hud.ClientBossBar;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;
import ru.sandfoxy.horizen.modules.ModuleManager;
import ru.sandfoxy.horizen.modules.features.render.NoRender;
import ru.sandfoxy.horizen.modules.ServerInfo;

import java.util.Map;
import java.util.UUID;

@Mixin(BossBarHud.class)
public class BossbarHudMixin {
    @Shadow
    Map<UUID, ClientBossBar> bossBars;

    @Inject(method = "render", at = @At("HEAD"), cancellable = true)
    private void render(DrawContext context, CallbackInfo ci) {
        ServerInfo.bossBars = this.bossBars;
        if (NoRender.renderFilter.getList().contains("Bossbar") && ModuleManager.getByName("NoRender").isEnabledRaw())  ci.cancel();
    }
}
