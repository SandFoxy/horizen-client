package ru.sandfoxy.horizen.utils.animations;


public class YawInterpolator {

    private final ContinualAnimation progressAnim = new ContinualAnimation();

    private float startYaw = 0f;
    private float endYaw = 0f;


    public static float wrapDegrees(float degrees) {
        degrees %= 360;
        if (degrees >= 180) degrees -= 360;
        if (degrees < -180) degrees += 360;
        return degrees;
    }

    public static float lerpAngle(float start, float end, float t) {
        float diff = wrapDegrees(end - start);
        return wrapDegrees(start + diff * t);
    }

    public void animateToYaw(float currentYaw, float targetYaw, int durationMs) {
        this.startYaw = wrapDegrees(currentYaw);
        this.endYaw = wrapDegrees(targetYaw);

        // Запускаем прогресс анимации от 0 до 1
        progressAnim.animate(1f, durationMs);
    }

    public float getSmoothYaw() {
        float progress = progressAnim.getOutput();
        // Ограничиваем progress [0,1]
        if (progress < 0f) progress = 0f;
        if (progress > 1f) progress = 1f;

        return lerpAngle(startYaw, endYaw, progress);
    }

    public boolean isDone() {
        return progressAnim.isDone();
    }
}