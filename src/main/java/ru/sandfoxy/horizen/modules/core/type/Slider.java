package ru.sandfoxy.horizen.modules.core.type;

import imgui.ImGui;
import ru.sandfoxy.horizen.modules.core.Setting;

public class Slider extends Setting {
    float[] value;
    private final float min;
    private final float max;
    private final SliderType type;

    public Slider(float value, float min, float max, String name, SliderType type) {
        super(value, name);
        this.value = new float[]{value};
        this.min = min;
        this.max = max;
        this.type = type;
    }

    @Override
    public void render(){
        if (!(boolean) this.isVisible.get()) return;

        ImGui.text(this.getName());
        ImGui.setNextItemWidth(250f);
        if (this.type == SliderType.INT){
            int[] temp = new int[]{(int) value[0]};

            ImGui.sliderInt("##Slider" + this.getName(), temp, (int)min,(int) max);

            this.value[0] = temp[0];

        } else if (this.type == SliderType.FLOAT) {
            ImGui.sliderFloat("##Slider" + this.getName(), this.value, min, max, "%.2f", 1);
        }
    }

    @Override
    public float getHeight(){
        return (boolean) this.isVisible.get() ? 32f : 0f;
    }

    @Override
    public void set(Object value) {
        this.value[0] = (float) value;
    }


    public float getFloat() {
        return this.value[0];
    }

    public int getInt() {
        return (int) this.value[0];
    }

    @Override
    public Object get(){
        return this.getFloat();
    }

    public enum SliderType{
        INT,
        FLOAT
    }
}
