package ru.sandfoxy.horizen.modules.core.type;

import imgui.ImGui;
import ru.sandfoxy.horizen.modules.core.Setting;

import java.util.List;

public class Separator extends Setting {
    public Separator() {
        super(0, "");
    }

    @Override
    public float getHeight() {
        return 0.f;
    }

    @Override
    public void render(){
        ImGui.separator();
    }

    @Override
    public boolean shouldBeSaved(){
        return false;
    }
}
