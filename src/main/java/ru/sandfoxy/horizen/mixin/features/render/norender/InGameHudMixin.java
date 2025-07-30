package ru.sandfoxy.horizen.mixin.features.render.norender;

import imgui.ImGui;
import net.minecraft.client.MinecraftClient;
import net.minecraft.client.gui.DrawContext;
import net.minecraft.client.gui.hud.InGameHud;
import net.minecraft.client.render.RenderTickCounter;
import net.minecraft.scoreboard.ScoreboardObjective;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;
import ru.sandfoxy.horizen.imgui.ImGuiUils;
import ru.sandfoxy.horizen.imgui.notifications.NotificationManager;
import ru.sandfoxy.horizen.imgui.utils.FontManager;
import ru.sandfoxy.horizen.modules.ModuleManager;
import ru.sandfoxy.horizen.modules.features.misc.Unhook;
import ru.sandfoxy.horizen.modules.features.render.NoRender;
import ru.sandfoxy.horizen.utils.SoundManager;

import static ru.sandfoxy.horizen.ModEntryPoint.menuOpened;
import static ru.sandfoxy.horizen.ModEntryPoint.LOGGER;
import static ru.sandfoxy.horizen.imgui.ClickGUI.*;

import ru.sandfoxy.horizen.modules.core.Module;

import java.awt.*;

@Mixin(InGameHud.class)
public class InGameHudMixin {

    @Inject(method = "render", at = @At("HEAD"))
    private void onRenderHead(DrawContext context, RenderTickCounter tickCounter, CallbackInfo ci) {
        if ((!menuOpened && !MinecraftClient.getInstance().inGameHud.getChatHud().isChatFocused()) && !Unhook.cheatUnloaded){
            imGuiGlfw.newFrame();
            ImGui.newFrame();

            ImGui.pushFont(FontManager.StemBold12);
            setupDocking("Overlay", "overlay-dockspace");

            for (Module module : ModuleManager.getModules()){
                try {
                    if (module.isEnabled()) module.onDraw();
                } catch (Exception e) {
                    NotificationManager.getInstance().addNotification("Exception in module " + module.getName() + " (Render)", e.toString(), 15000);
                    LOGGER.error("Exception in module " + module.getName() + " (onDraw): ", e);
                    SoundManager.playErrorSound();
                    module.toggle(false);
                }
            }
            ImGuiUils.drawKeybinds();
            for (Module module : ModuleManager.getModules()){
                try {
                    if (module.isEnabled()) module.onGui();
                } catch (Exception e) {
                    NotificationManager.getInstance().addNotification("Exception in module " + module.getName() + " (Render)", e.toString(), 15000);
                    LOGGER.error("Exception in module " + module.getName() + " (onGui): ", e);
                    SoundManager.playErrorSound();
                    module.toggle(false);
                }
            }

            ImGui.popFont();
            finishDocking();

            ImGui.render();
            endFrame(windowHandle);
        }
    }

    @Inject(method = "renderScoreboardSidebar(Lnet/minecraft/client/gui/DrawContext;Lnet/minecraft/scoreboard/ScoreboardObjective;)V", at = @At("HEAD"), cancellable = true)
    private void render(DrawContext drawContext, ScoreboardObjective objective, CallbackInfo ci) {
        if (NoRender.renderFilter.getList().contains("Scoreboard") && ModuleManager.getByName("NoRender").isEnabledRaw())  ci.cancel();
    }
}
