package ru.sandfoxy.horizen.modules.core.type;

import imgui.ImGui;
import ru.sandfoxy.horizen.modules.core.Setting;

import java.util.function.Supplier;

public class Checkbox extends Setting {
    public Checkbox(boolean value, String name) {
        super(value, name);
    }

    @Override
    public void render(){
        if (!(boolean) this.isVisible.get()) return;

        if (ImGui.checkbox(this.getName(), (Boolean) this.get())){
            this.set((Object) !(Boolean) this.get());
        }
    }

    public boolean getValue(){
        return (boolean) this.get();
    }
}
