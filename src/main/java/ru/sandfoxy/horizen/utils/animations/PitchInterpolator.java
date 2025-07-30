package ru.sandfoxy.horizen.utils.animations;

public class PitchInterpolator {

    private final ContinualAnimation progressAnim = new ContinualAnimation();

    private float startPitch = 0f;
    private float endPitch = 0f;

    private final float maxStep; // Максимальный шаг изменения pitch за тик, например 1.0f градус

    public PitchInterpolator(float maxStep) {
        this.maxStep = maxStep;
    }

    public void animateToPitch(float currentPitch, float targetPitch, int durationMs) {
        this.startPitch = currentPitch;
        this.endPitch = targetPitch;

        progressAnim.animate(1f, durationMs);
    }

    public float getSmoothPitch(float currentPitch) {
        float progress = progressAnim.getOutput();

        if (progress < 0f) progress = 0f;
        if (progress > 1f) progress = 1f;

        float interpolatedPitch = startPitch + (endPitch - startPitch) * progress;

        float delta = interpolatedPitch - currentPitch;

        if (Math.abs(delta) > maxStep) {
            interpolatedPitch = currentPitch + Math.signum(delta) * maxStep;
        }

        return interpolatedPitch;
    }

    public boolean isDone() {
        return progressAnim.isDone();
    }
}