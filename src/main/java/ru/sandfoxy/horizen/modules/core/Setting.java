package ru.sandfoxy.horizen.modules.core;

import java.util.function.Supplier;

public abstract class Setting<T> {
    private final String name;
    public Supplier<Boolean> isVisible = new Supplier<Boolean>() {
        @Override
        public Boolean get() {
            return true;
        }
    };
    private T value;

    public Setting(T defaultValue, String name) {
        this.name = name;
        this.value = defaultValue;
    }

    public String getName() {
        return name;
    }

    public T get() {
        return value;
    }

    public void set(Object value) {
        this.value = (T) value;
    }

    public boolean shouldBeSaved(){
        return true;
    }

    public float getHeight(){
        return this.isVisible.get() ? 16f : 0f;
    }

    public void render() {

    }

    public void visibleIf(Supplier<Boolean> condition) {
        this.isVisible = condition;
    }
}
