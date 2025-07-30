package ru.sandfoxy.horizen.modules.core.type;

import ru.sandfoxy.horizen.modules.core.Setting;

import java.util.List;

public class SaveableList extends Setting {
    List<String> values = new java.util.ArrayList<>();

    public SaveableList(String name) {
        super(0, name);
    }

    public List<String> getList() {
        return values;
    }

    @Override
    public Object get(){
        return values;
    }

    public void add(String str){
        values.add(str);
    }

    public void remove(String str){
        values.remove(str);
    }

    @Override
    public void set(Object value) {
        values.clear();
        List<String> vals = (List<String>) value;
        for (String val : vals)
            values.add(val);
    }

    @Override
    public float getHeight() {
        return 0.f;
    }
}
