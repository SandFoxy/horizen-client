package ru.sandfoxy.horizen.imgui.notifications;

import imgui.ImGui;
import imgui.flag.ImGuiCol;
import imgui.flag.ImGuiStyleVar;
import imgui.flag.ImGuiWindowFlags;
import ru.sandfoxy.horizen.utils.animations.Animation;
import ru.sandfoxy.horizen.utils.animations.Direction;
import ru.sandfoxy.horizen.utils.animations.impl.DecelerateAnimation;

public class Notification {
    private static int nextId = 0;
    private final int id;
    private String title;
    private String description;
    private long creationTime;
    private long duration;
    private boolean isRemoving;

    private Animation fadeAnimation;
    private Animation slideAnimation;
    private Animation scaleAnimation;

    private float calculatedWidth;
    private float calculatedHeight;
    private static final float MIN_WIDTH = 250f;
    private static final float MAX_WIDTH = 400f;
    private static final float BASE_HEIGHT = 60f;
    private static final float LINE_HEIGHT = 20f;
    private static final float PADDING = 15f;

    public Notification(String title, String description, long duration) {
        this.id = nextId++;
        this.title = title;
        this.description = description;
        this.creationTime = System.currentTimeMillis();
        this.duration = duration;
        this.isRemoving = false;

        this.fadeAnimation = new DecelerateAnimation(500, 1.0, Direction.FORWARDS);
        this.slideAnimation = new DecelerateAnimation(500, 1.0, Direction.FORWARDS);
        this.scaleAnimation = new DecelerateAnimation(300, 1.0, Direction.FORWARDS);

        calculateDynamicSize();
    }
    
    private void calculateDynamicSize() {
        float titleWidth = Math.max(title.length() * 8f, 0f);
        float descWidth = Math.max(description.length() * 6f, 0f);
        
        calculatedWidth = Math.max(MIN_WIDTH, Math.min(MAX_WIDTH, Math.max(titleWidth, descWidth) + PADDING * 2));

        int titleLines = Math.max(1, (int) Math.ceil(titleWidth / (calculatedWidth - PADDING * 2)));
        int descLines = Math.max(1, (int) Math.ceil(descWidth / (calculatedWidth - PADDING * 2)));
        
        calculatedHeight = BASE_HEIGHT + (titleLines + descLines - 2) * LINE_HEIGHT;
    }

    public void update() {
        long currentTime = System.currentTimeMillis();
        long elapsed = currentTime - creationTime;

        if (!isRemoving) {
            if (elapsed >= duration) {
                startRemoval();
            }
        }
    }
    
    public void startRemoval() {
        if (!isRemoving) {
            isRemoving = true;
            fadeAnimation.setDirection(Direction.BACKWARDS);
            slideAnimation.setDirection(Direction.BACKWARDS);
            scaleAnimation.setDirection(Direction.BACKWARDS);
        }
    }

    public void render() {
        float alpha = fadeAnimation.getOutput().floatValue();
        if (alpha <= 0.01f) return;

        float slideProgress = slideAnimation.getOutput().floatValue();
        float scaleProgress = scaleAnimation.getOutput().floatValue();

        ImGui.pushStyleVar(ImGuiStyleVar.Alpha, alpha);

        float scaledWidth = calculatedWidth * (0.8f + 0.2f * scaleProgress);
        float scaledHeight = calculatedHeight * (0.8f + 0.2f * scaleProgress);
        
        ImGui.setNextWindowSize(scaledWidth, scaledHeight);

        String windowName = title + "##notification_" + id;
        
        if (ImGui.begin(windowName,
                ImGuiWindowFlags.NoResize |
                ImGuiWindowFlags.NoMove |
                ImGuiWindowFlags.NoScrollbar |
                ImGuiWindowFlags.NoCollapse |
                ImGuiWindowFlags.NoTitleBar)) {

            ImGui.pushStyleColor(ImGuiCol.Text, 1.0f, 1.0f, 1.0f, 1.0f);
            ImGui.textWrapped(title);
            ImGui.popStyleColor();

            ImGui.spacing();

            ImGui.pushStyleColor(ImGuiCol.Text, 0.8f, 0.8f, 0.8f, 1.0f);
            ImGui.textWrapped(description);
            ImGui.popStyleColor();
        }
        ImGui.end();
        
        ImGui.popStyleVar(); // Alpha
    }

    public boolean shouldRemove() {
        return isRemoving && fadeAnimation.finished(Direction.BACKWARDS);
    }

    public float getYOffset() {
        float slideProgress = slideAnimation.getOutput().floatValue();
        return 100f * (1f - slideProgress);
    }

    public float getHeight() {
        float scaleProgress = scaleAnimation.getOutput().floatValue();
        return calculatedHeight * (0.8f + 0.2f * scaleProgress);
    }
    
    public float getWidth() {
        float scaleProgress = scaleAnimation.getOutput().floatValue();
        return calculatedWidth * (0.8f + 0.2f * scaleProgress);
    }
    
    public int getId() {
        return id;
    }
}