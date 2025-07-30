package ru.sandfoxy.horizen;

import com.mojang.blaze3d.systems.RenderSystem;
import imgui.ImGui;
import net.fabricmc.api.ClientModInitializer;
import net.fabricmc.fabric.api.client.event.lifecycle.v1.ClientTickEvents;
import net.fabricmc.fabric.api.client.rendering.v1.WorldRenderEvents;
import net.minecraft.client.MinecraftClient;
import net.minecraft.client.gui.DrawContext;
import net.minecraft.client.render.Camera;
import net.minecraft.client.render.RenderTickCounter;
import net.minecraft.client.util.math.MatrixStack;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.joml.Matrix4f;
import ru.sandfoxy.horizen.events.PacketEvent;
import ru.sandfoxy.horizen.imgui.interfaces.Renderable;
import java.util.ArrayList;

import ru.sandfoxy.horizen.imgui.notifications.NotificationManager;
import ru.sandfoxy.horizen.modules.ModuleManager;
import ru.sandfoxy.horizen.modules.core.Module;
import ru.sandfoxy.horizen.modules.ServerInfo;
import ru.sandfoxy.horizen.utils.math.WorldToScreen;
import ru.sandfoxy.horizen.utils.SoundManager;

public class ModEntryPoint implements ClientModInitializer {
    public static final String MODID = "Horizen Client";
    public static final Logger LOGGER = LogManager.getLogger(MODID);
    public static ArrayList<Renderable> renderstack = new ArrayList<>();
    public static boolean menuOpened = true;
    public static boolean lockInput = false;
    public static ArrayList<Renderable> toRemove = new ArrayList<>();

    @Override
    public void onInitializeClient() {

        LOGGER.info("Started with debug.");
        LOGGER.info("Initializing ModuleManager...");
        ModuleManager.init();
        LOGGER.info("Initializing SoundManager...");
        SoundManager.initialize();
        LOGGER.info("Registering events...");

        ClientTickEvents.START_CLIENT_TICK.register(client -> {
            ServerInfo.update();

            for (Module module : ModuleManager.getModules()) {
                try {
                    if (module.isEnabled()) module.startTick();
                } catch (Exception e) {
                    NotificationManager.getInstance().addNotification("Exception in module " + module.getName() + " (Tick Start)", e.toString(), 15000);
                    LOGGER.error("Exception in module " + module.getName() + " (Tick Start): ", e);
                    SoundManager.playErrorSound();
                    module.toggle(false);
                }
            }
        });
        ClientTickEvents.END_CLIENT_TICK.register(client -> {
            for (Module module : ModuleManager.getModules()) {
                try {
                    if (module.isEnabled()) module.endTick();
                } catch (Exception e) {
                    NotificationManager.getInstance().addNotification("Exception in module " + module.getName() + " (Tick End)", e.toString(), 15000);
                    LOGGER.error("Exception in module " + module.getName() + " (Tick End): ", e);
                    SoundManager.playErrorSound();
                    module.toggle(false);
                }
            }
        });

        WorldRenderEvents.END.register(ctx -> {
            MinecraftClient client = MinecraftClient.getInstance();
            Camera camera = ctx.camera();
            MatrixStack matrices = ctx.matrixStack();

            WorldToScreen.projection = new Matrix4f(RenderSystem.getProjectionMatrix());
            WorldToScreen.tickDelta = ctx.tickCounter().getTickDelta(true);
        });

        PacketEvent.RECEIVE.register(event -> {
            for (Module module : ModuleManager.getModules()) {
                try {
                    if (module.isEnabled()) module.packetReceived(event);
                    if (event.isCancelled()) break;

                } catch (Exception e) {
                    NotificationManager.getInstance().addNotification("Exception in module " + module.getName() + " (Packet Received)", e.toString(), 15000);
                    LOGGER.error("Exception in module " + module.getName() + " (Packet Received): ", e);
                    SoundManager.playErrorSound();
                    module.toggle(false);
                }
            }
        });
        PacketEvent.SEND.register(event -> {
            for (Module module : ModuleManager.getModules()) {
                try {
                    if (module.isEnabled()) module.packetSend(event);
                    if (event.isCancelled()) break;

                } catch (Exception e) {
                    NotificationManager.getInstance().addNotification("Exception in module " + module.getName() + " (Packet Send)", e.toString(), 15000);
                    LOGGER.error("Exception in module " + module.getName() + " (Packet Send): ", e);
                    SoundManager.playErrorSound();
                    module.toggle(false);
                }
            }
        });

        LOGGER.info("All Done!");
    }

    public static Renderable pushRenderable(Renderable renderable) {
        renderstack.add(renderable);
        return renderable;
    }

    public static Renderable pullRenderable(Renderable renderable) {
        renderstack.remove(renderable);
        return renderable;
    }

    public static Renderable pullRenderableAfterRender(Renderable renderable) {
        toRemove.add(renderable);
        return renderable;
    }

    public static boolean shouldCancelGameKeyboardInputs() {
        return ImGui.isAnyItemActive() || ImGui.isAnyItemFocused();
    }
}
