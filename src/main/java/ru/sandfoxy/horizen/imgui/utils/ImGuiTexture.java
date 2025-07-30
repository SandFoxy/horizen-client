package ru.sandfoxy.horizen.imgui.utils;

public class ImGuiTexture {
    public float height = 0f;
    public float width = 0;
    public int textureId = 0;

    public ImGuiTexture(int textureId, float width, float height){
        this.height = height;
        this.width = width;
        this.textureId = textureId;
    }
}
