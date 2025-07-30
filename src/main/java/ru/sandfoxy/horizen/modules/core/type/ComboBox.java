package ru.sandfoxy.horizen.modules.core.type;

import imgui.ImGui;
import imgui.type.ImInt;
import ru.sandfoxy.horizen.modules.core.Setting;

import java.util.Arrays;
import java.util.List;

public class ComboBox extends Setting {
    List<String> options;
    ImInt selectedIndex;

    public ComboBox(String name, String default_value, List<String> options) {
        super(options, name);
        this.selectedIndex = new ImInt(options.indexOf(default_value));
        this.options = options;
    }

    @Override
    public void render(){
        if (!(boolean) this.isVisible.get()) return;

        ImGui.text(this.getName());
        ImGui.setNextItemWidth(250f);

        ImGui.combo("##Combo" + this.getName(), selectedIndex, options.toArray(new String[0]));
    }

    @Override
    public Object get(){
        return this.options.get(selectedIndex.get());
    }

    public String getValue(){
        return this.options.get(selectedIndex.get());
    }

    public boolean isEquals(String value){
        return this.options.get(selectedIndex.get()).equals(value);
    }

    @Override
    public void set(Object value){
        this.selectedIndex = new ImInt(this.options.indexOf((String) value));
    }

    @Override
    public float getHeight(){
        return (boolean) this.isVisible.get() ? 32f : 0f;
    }
}
