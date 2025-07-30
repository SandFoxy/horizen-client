package ru.sandfoxy.horizen.utils.math;

import imgui.ImGui;
import net.minecraft.client.MinecraftClient;
import net.minecraft.client.render.Camera;
import net.minecraft.util.math.Vec3d;
import org.joml.Matrix4f;
import org.joml.Vector4f;


public class WorldToScreen {
    private static MinecraftClient mc = MinecraftClient.getInstance();
    public static Matrix4f projection;
    public static float tickDelta;

    public static Vec3d w2s(Vec3d pos) {
        Camera camera = mc.gameRenderer.getCamera();

        if (camera == null) return null;

        Matrix4f modelView = new Matrix4f()
                .rotateX(camera.getPitch() * ((float)Math.PI / 180F))
                .rotateY((camera.getYaw() + 180.0F) * ((float)Math.PI / 180F))
                .translate((float) -camera.getPos().x, (float) -camera.getPos().y, (float) -camera.getPos().z);

        Vector4f vec = new Vector4f((float) pos.x, (float) pos.y, (float) pos.z, 1.0f);

        vec.mul(modelView);
        vec.mul(projection);

        if (vec.w <= 0.0f) return null;

        vec.div(vec.w);

        float screenX = (vec.x * 0.5f + 0.5f) * ImGui.getIO().getDisplaySizeX();
        float screenY = (1.0f - (vec.y * 0.5f + 0.5f)) * ImGui.getIO().getDisplaySizeY();

        return new Vec3d(screenX, screenY, vec.z);
    }
}