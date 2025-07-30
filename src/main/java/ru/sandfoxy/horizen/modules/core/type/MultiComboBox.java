package ru.sandfoxy.horizen.modules.core.type;

import imgui.ImGui;
import imgui.flag.ImGuiSelectableFlags;
import ru.sandfoxy.horizen.modules.core.Setting;

import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

public class MultiComboBox extends Setting {
    List<String> options;
    Set<Integer> selectedIndices = new HashSet<>();

    public MultiComboBox(String name, List<String> defaultValues, List<String> options) {
        super(options, name);
        this.options = options;
        for (String val : defaultValues)
            if (options.contains(val))
                selectedIndices.add(options.indexOf(val));
    }

    @Override
    public void render() {
        if (!(boolean) this.isVisible.get()) return;

        ImGui.text(this.getName());
        ImGui.setNextItemWidth(250f);

        if (ImGui.beginCombo("##MultiCombo" + this.getName(), getSelectedText())) {
            for (int i = 0; i < options.size(); i++) {
                boolean selected = selectedIndices.contains(i);
                if (ImGui.selectable(options.get(i), selected, ImGuiSelectableFlags.DontClosePopups)) {
                    if (selected && selectedIndices.size() > 1)
                        selectedIndices.remove(i);
                    else
                        selectedIndices.add(i);
                }
            }
            ImGui.endCombo();
        }
    }

    private String getSelectedText() {
        List<String> selected = new ArrayList<>();
        for (int i : selectedIndices)
            selected.add(options.get(i));
        return String.join(", ", selected);
    }

    public List<String> getList() {
        List<String> selected = new ArrayList<>();
        for (int i : selectedIndices)
            selected.add(options.get(i));
        return selected;
    }

    @Override
    public void set(Object value) {
        selectedIndices.clear();
        List<String> vals = (List<String>) value;
        for (String val : vals)
            if (options.contains(val))
                selectedIndices.add(options.indexOf(val));
    }

    @Override
    public Object get(){
        return this.getList();
    }

    @Override
    public float getHeight() {
        return (boolean) this.isVisible.get() ? 32f : 0.f;
    }
}
