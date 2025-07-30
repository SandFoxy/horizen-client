package ru.sandfoxy.horizen.imgui.screen;

import imgui.ImGui;
import imgui.type.ImBoolean;
import net.minecraft.component.Component;
import ru.sandfoxy.horizen.ModEntryPoint;
import ru.sandfoxy.horizen.imgui.interfaces.Renderable;
import ru.sandfoxy.horizen.imgui.interfaces.Theme;

public class ImGuiWindow implements Renderable {
    Theme theme;

    Component name;

    WindowRenderer renderer;

    public boolean canClose;

    ImBoolean open;

    public ImGuiWindow(Theme theme, Component name, WindowRenderer renderer, boolean canClose) {
        this.theme = theme;
        this.name = name;
        this.renderer = renderer;
        this.canClose = canClose;
        this.open = new ImBoolean(true);
    }

    @Override
    public String getName() {
        return this.name.toString();
    }

    @Override
    public Theme getTheme() {
        return this.theme;
    }

    @Override
    public void render() {
        if (!open.get()) {
            ModEntryPoint.pullRenderableAfterRender(this);
            return;
        }

        if (canClose) {
            ImGui.begin(getName(), open);
        } else {
            ImGui.begin(getName());
        }

        renderer.renderWindow();

        ImGui.end();
    }
}
