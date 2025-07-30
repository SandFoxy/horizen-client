package ru.sandfoxy.horizen.imgui.screen;

import net.minecraft.client.gui.DrawContext;
import net.minecraft.client.gui.screen.Screen;
import net.minecraft.component.Component;
import net.minecraft.text.Text;
import ru.sandfoxy.horizen.ModEntryPoint;

import java.util.List;

public class ImGuiScreen extends Screen {

    List<ImGuiWindow> windows;

    boolean closeWhenNoWindows;

    boolean alreadyInitialised;

    protected ImGuiScreen(Component component, boolean closeWhenNoWindows) {
        super(Text.of(component.toString()));
        this.closeWhenNoWindows = closeWhenNoWindows;
        alreadyInitialised = false;
    }

    protected List<ImGuiWindow> initImGui() {
        return List.of();
    }

    @Override
    protected void init() {
        super.init();
        if (!alreadyInitialised) {
            windows = initImGui();
            for (ImGuiWindow window : windows) {
                ModEntryPoint.pushRenderable(window);
            }
            alreadyInitialised = true;
        }
    }

    @Override
    public void close() {
        for (ImGuiWindow window : windows) {
            ModEntryPoint.pullRenderable(window);
        }
        super.close();
    }

    @Override
    public void render(DrawContext guiGraphics, int i, int j, float f) {
        super.render(guiGraphics, i, j, f);

        if (closeWhenNoWindows) {
            boolean foundOpen = false;
            for (ImGuiWindow window : windows) {
                if (window.open.get()) {
                    foundOpen = true;
                }
            }
            if (!foundOpen)
                close();
        }
    }

    protected void pushWindow(ImGuiWindow window) {
        windows.add(window);
        ModEntryPoint.pushRenderable(window);
    }

    protected void pullWindow(ImGuiWindow window) {
        windows.remove(window);
        ModEntryPoint.pullRenderableAfterRender(window);
    }
}
