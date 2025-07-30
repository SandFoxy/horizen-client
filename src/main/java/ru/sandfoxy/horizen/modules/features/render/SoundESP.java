package ru.sandfoxy.horizen.modules.features.render;

import net.minecraft.client.MinecraftClient;
import net.minecraft.client.sound.SoundInstance;
import net.minecraft.util.math.Vec3d;
import ru.sandfoxy.horizen.events.OnSoundPlayedEvent;
import ru.sandfoxy.horizen.imgui.ImGuiUils;
import ru.sandfoxy.horizen.modules.core.Module;
import ru.sandfoxy.horizen.modules.core.type.Slider;
import ru.sandfoxy.horizen.utils.math.WorldToScreen;

import java.awt.*;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

import static ru.sandfoxy.horizen.utils.render.RenderUtils.ConvertColor;

public class SoundESP extends Module {
    private final MinecraftClient mc = MinecraftClient.getInstance();

    private static final Map<SoundInstance, Long> soundMap = new ConcurrentHashMap<>();
    
    private final Slider maxSounds = new Slider(50f, 1f, 200f, "Max Sounds", Slider.SliderType.INT);
    private final Slider soundLifetime = new Slider(5f, 1f, 30f, "Lifetime (sec)", Slider.SliderType.INT);

    public SoundESP() {
        super("SoundESP", CATEGORY.RENDER, "Shows sound sources in the world");
        
        this.addSetting(maxSounds);
        this.addSetting(soundLifetime);

        OnSoundPlayedEvent.EVENT.register((sound) -> {
            soundMap.put(sound, System.currentTimeMillis());

            if (soundMap.size() > maxSounds.getInt()) {
                SoundInstance oldestSound = soundMap.entrySet().stream()
                    .min(Map.Entry.comparingByValue())
                    .map(Map.Entry::getKey)
                    .orElse(null);
                
                if (oldestSound != null) {
                    soundMap.remove(oldestSound);
                }
            }
        });
    }

    @Override
    public void onDraw() {
        if (mc.player == null || mc.world == null) return;

        long currentTime = System.currentTimeMillis();
        long maxAge = (long) (soundLifetime.getInt() * 1000);
        
        soundMap.entrySet().removeIf(entry -> 
            currentTime - entry.getValue() > maxAge
        );

        for (Map.Entry<SoundInstance, Long> entry : soundMap.entrySet()) {
            SoundInstance sound = entry.getKey();
            long soundTime = entry.getValue();
            
            try {
                Vec3d soundPos = new Vec3d(sound.getX(), sound.getY(), sound.getZ());

                Vec3d screenPos = WorldToScreen.w2s(soundPos);
                
                if (screenPos == null) continue;

                double distance = mc.player.getPos().distanceTo(soundPos);

                String soundName = sound.getId().getPath();

                if (soundName.contains(".")) {
                    String[] parts = soundName.split("\\.");
                    soundName = parts[parts.length - 1];
                }
                soundName = soundName.replace("_", " ");

                float age = (currentTime - soundTime) / 1000.0f;
                float maxLifetime = soundLifetime.getInt();
                float timeRemaining = maxLifetime - age;
                float alpha = Math.max(0.0f, Math.min(1.0f, timeRemaining / maxLifetime));

                Color color = new Color(1.0f, 1.0f, 1.0f, alpha);

                ImGuiUils.drawTextCentred(
                        soundName,
                    (float) screenPos.x, 
                    (float) screenPos.y,
                    11.0f,
                    ConvertColor(color),
                    true
                );
                
            } catch (Exception e) {
                soundMap.remove(sound);
            }
        }
    }
}
