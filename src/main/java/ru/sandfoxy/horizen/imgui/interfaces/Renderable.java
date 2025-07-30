package ru.sandfoxy.horizen.imgui.interfaces;

public interface Renderable {
    String getName();
    Theme getTheme();

    void render();
}
