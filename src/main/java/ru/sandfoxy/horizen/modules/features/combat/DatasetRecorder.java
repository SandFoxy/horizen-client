package ru.sandfoxy.horizen.modules.features.combat;

import imgui.ImGui;
import imgui.flag.ImGuiWindowFlags;
import ru.sandfoxy.horizen.modules.core.Module;

public class DatasetRecorder extends Module {
    public static int linesRecorded = 0;
    public static boolean wasRecordingStarted = false;

    public DatasetRecorder() {
        super("Dataset Recorder", CATEGORY.COMBAT, "Will record a dataset for neuro killaura");
    }

    @Override
    public void onDraw() {
        ImGui.begin("Dataset Info", ImGuiWindowFlags.AlwaysAutoResize | ImGuiWindowFlags.NoResize | ImGuiWindowFlags.NoCollapse);
        if (wasRecordingStarted){
            ImGui.text("Hit's recorded: " + (linesRecorded - 1));
            if (linesRecorded > 1){
                ImGui.progressBar((float) linesRecorded / 2000);
            }
        }else {
            ImGui.text("Hit someone to start.");
        }
        ImGui.end();
    }
}
