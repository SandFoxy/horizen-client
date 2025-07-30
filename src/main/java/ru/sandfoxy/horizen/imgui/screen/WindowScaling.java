package ru.sandfoxy.horizen.imgui.screen;


import net.minecraft.client.util.Window;
import net.minecraft.client.MinecraftClient;
import org.joml.Vector2d;

public class WindowScaling {

    public static int X_OFFSET = 0;
    public static int Y_OFFSET = 0;
    public static int Y_TOP_OFFSET = 0;

    public static int WIDTH = 0;
    public static int HEIGHT = 0;

    public static boolean DISABLE_POST_PROCESSORS = false;

    private static Window getGameWindow() {
        return MinecraftClient.getInstance().getWindow();
    }

    public static Vector2d scalePoint(Vector2d point) {
        return scalePoint(point.x, point.y);
    }

    public static Vector2d scalePoint(double x, double y) {
        Window window = getGameWindow();

        float x_scale = (float) WIDTH / window.getWidth();
        float y_scale = (float) HEIGHT / window.getHeight();

        x *= x_scale;
        y *= y_scale;

        x += X_OFFSET;
        y += Y_OFFSET;

        return new Vector2d(x, y);
    }

    public static Vector2d unscalePoint(Vector2d point) {
        return unscalePoint(point.x, point.y);
    }

    public static Vector2d unscalePoint(double x, double y) {
        Window window = getGameWindow();

        float x_scale = (float) WIDTH / window.getWidth();
        float y_scale = (float) HEIGHT / window.getHeight();

        x -= X_OFFSET;
        y -= Y_OFFSET;

        x /= x_scale;
        y /= y_scale;

        return new Vector2d(x, y);
    }

    public static Vector2d scaleWidthHeight(double width, double height) {
        Window window = getGameWindow();

        float x_scale = (float) WIDTH / window.getWidth();
        float y_scale = (float) HEIGHT / window.getHeight();

        width *= x_scale;
        height *= y_scale;

        return new Vector2d(width, height);
    }

    public static Vector2d unscaleWidthHeight(double width, double height) {
        Window window = getGameWindow();

        float x_scale = (float) WIDTH / window.getWidth();
        float y_scale = (float) HEIGHT / window.getHeight();

        width /= x_scale;
        height /= y_scale;

        return new Vector2d(width, height);
    }

    public static boolean isChanged() {
        Window window = getGameWindow();
        return !(window.getWidth() == WIDTH && window.getHeight() == HEIGHT && X_OFFSET == 0 && Y_OFFSET == 0);
    }


    public static void update() {
        DISABLE_POST_PROCESSORS = isChanged();
    }

}
