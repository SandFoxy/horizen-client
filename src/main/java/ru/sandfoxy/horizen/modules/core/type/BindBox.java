package ru.sandfoxy.horizen.modules.core.type;

import imgui.ImGui;
import ru.sandfoxy.horizen.imgui.utils.FontManager;
import ru.sandfoxy.horizen.modules.core.Setting;
import ru.sandfoxy.horizen.utils.others.GLFWKeyMapper;

import static org.lwjgl.glfw.GLFW.GLFW_KEY_ESCAPE;
import static ru.sandfoxy.horizen.utils.others.GLFWKeyMapper.GLFW_KEY_CODES;

public class BindBox extends Setting {
    private int keybindButton = -1;

    public BindBox(String name) {
        super(-1, name);
    }

    @Override
    public void render(){
        if (!(boolean) this.isVisible.get()) return;

        if (keybindButton == -5){
            for (int key : GLFW_KEY_CODES) {

                if (ImGui.isKeyDown(key)) {
                    if (key == GLFW_KEY_ESCAPE && this.keybindButton == -5) {
                        this.keybindButton = -1;
                        return;
                    }
                    this.keybindButton = key;
                    return;
                }
            }
        }


        ImGui.alignTextToFramePadding(); // Align the text vertically to the button
        ImGui.text(this.getName());

        ImGui.sameLine();
        if (ImGui.button(GLFWKeyMapper.getKeyName(keybindButton) + "##" + this.getName())) {
            this.keybindButton = -5;
        }

    }

    @Override
    public void set(Object key){
        this.keybindButton = (int) key;
    }

    @Override
    public Object get(){
        return this.keybindButton;
    }

    public boolean isDown(){
        return this.keybindButton > 0 && ImGui.isKeyDown(this.keybindButton);
    }

    public boolean isBinded(){
        return this.keybindButton > 0;
    }
}
