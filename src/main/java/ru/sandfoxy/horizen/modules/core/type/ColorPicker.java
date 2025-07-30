package ru.sandfoxy.horizen.modules.core.type;

import imgui.ImGui;
import imgui.flag.ImGuiColorEditFlags;
import ru.sandfoxy.horizen.imgui.utils.FontManager;
import ru.sandfoxy.horizen.modules.core.Setting;

import java.awt.*;

import static ru.sandfoxy.horizen.utils.render.RenderUtils.ConvertColor;

public class ColorPicker extends Setting {
    private float[] color;

    public ColorPicker(String name, Color default_color) {
        super(ConvertColor(default_color), name);

        this.color = new float[]{
                default_color.getRed() / 255f,
                default_color.getGreen() / 255f,
                default_color.getBlue() / 255f,
                default_color.getAlpha() / 255f
        };
    }

    public float[] getColorFloat() {
        return color;
    }

    public Color getColor() {
        return new Color(this.color[0], this.color[1],this.color[2]);
    }

    @Override
    public Object get() {
        return ConvertColor(new Color(this.color[0], this.color[1],this.color[2]));
    }

    public int getIntColor(){
        return ConvertColor(new Color(this.color[0], this.color[1],this.color[2]));
    }

    public void setColor(float r, float g, float b, float a) {
        color[0] = r;
        color[1] = g;
        color[2] = b;
        color[3] = a;
    }

    @Override
    public void render() {
        if (isVisible != null && !((boolean) isVisible.get())) return;

        ImGui.pushFont(FontManager.StemBold14);
        ImGui.alignTextToFramePadding();
        ImGui.text(this.getName());
        ImGui.popFont();

        ImGui.sameLine();

        ImGui.setCursorPosX(ImGui.getWindowWidth() - 25);
        ImGui.colorEdit4("##" + this.getName(), color, ImGuiColorEditFlags.NoInputs | ImGuiColorEditFlags.NoTooltip);
    }

    @Override
    public void set(Object value) {
        Color temp = new Color((int) value);
        this.color = new float[]{temp.getRed() / 255f, temp.getGreen() / 255f, temp.getBlue() / 255f, temp.getAlpha() / 255f};
    }


    @Override
    public float getHeight() {
        return (boolean) this.isVisible.get() ? 16f : 0.f;
    }
}
