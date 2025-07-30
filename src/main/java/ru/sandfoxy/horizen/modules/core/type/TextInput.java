package ru.sandfoxy.horizen.modules.core.type;

import imgui.ImGui;
import imgui.type.ImString;
import ru.sandfoxy.horizen.modules.core.Setting;

public class TextInput extends Setting {
    ImString inputValue = new ImString();

    public TextInput(String name) {
        super("", name);
    }

    @Override
    public void render(){
        if (!(boolean) this.isVisible.get()) return;

        ImGui.inputTextWithHint("##"+this.getName(), this.getName(), this.inputValue);
    }

    @Override
    public void set(Object obj){
        inputValue.set((String) obj);
    }

    @Override
    public Object get(){
        return inputValue.get();
    }

    public String getInput(){
        return inputValue.get();
    }
}
