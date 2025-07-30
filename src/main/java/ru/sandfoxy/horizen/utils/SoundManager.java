package ru.sandfoxy.horizen.utils;

import net.minecraft.client.MinecraftClient;
import net.minecraft.client.sound.PositionedSoundInstance;
import net.minecraft.registry.Registries;
import net.minecraft.registry.Registry;
import net.minecraft.sound.SoundEvent;
import net.minecraft.util.Identifier;
import net.minecraft.util.math.random.Random;

import java.util.HashMap;
import java.util.Map;

/**
 * SoundManager - Utility class for loading and playing custom sounds in Horizen Client
 * 
 * Usage Examples:
 * 
 * // Play specific sounds:
 * SoundManager.playToggleSound();
 * SoundManager.playEnableSound();
 * SoundManager.playDisableSound();
 * SoundManager.playErrorSound();
 * 
 * // Play sounds with custom volume/pitch:
 * SoundManager.playSound("toggle_1", 0.8f, 1.2f);
 * SoundManager.playSoundLocally("error_1", 1.0f, 0.8f);
 * 
 * // Play sounds by name:
 * SoundManager.playSound(SoundManager.TOGGLE_SOUND);
 * SoundManager.playSound(SoundManager.ERROR_SOUND);
 * 
 * // Play random sound:
 * SoundManager.playRandomSound();
 * 
 * Available sounds: toggle_1, disable_1, enable_1, error_1
 */
public class SoundManager {
    private static final String MOD_ID = "horizen";
    private static final Map<String, SoundEvent> soundEvents = new HashMap<>();
    
    // Sound identifiers
    public static final String TOGGLE_SOUND = "toggle_1";
    public static final String DISABLE_SOUND = "disable_1"; 
    public static final String ENABLE_SOUND = "enable_1";
    public static final String ERROR_SOUND = "error_1";
    public static final String LOCATION_PING_SOUND = "location_ping";
    public static final String NOTIFICATION_DEFAULT_SOUND = "notification_default";

    // Registered sound events
    public static SoundEvent TOGGLE_1;
    public static SoundEvent DISABLE_1;
    public static SoundEvent ENABLE_1;
    public static SoundEvent ERROR_1;
    public static SoundEvent LOCATION_PING;
    public static SoundEvent NOTIFICATION_DEFAULT;

    public static void initialize() {
        TOGGLE_1 = registerSound(TOGGLE_SOUND);
        DISABLE_1 = registerSound(DISABLE_SOUND);
        ENABLE_1 = registerSound(ENABLE_SOUND);
        ERROR_1 = registerSound(ERROR_SOUND);
        LOCATION_PING = registerSound(LOCATION_PING_SOUND);
        NOTIFICATION_DEFAULT = registerSound(NOTIFICATION_DEFAULT_SOUND);
    }

    private static SoundEvent registerSound(String soundName) {
        Identifier soundId = Identifier.of(MOD_ID, soundName);
        SoundEvent soundEvent = SoundEvent.of(soundId);
        soundEvents.put(soundName, soundEvent);
        return Registry.register(Registries.SOUND_EVENT, soundId, soundEvent);
    }

    public static void playSound(String soundName) {
        playSound(soundName, 1.0f, 1.0f);
    }

    public static void playSound(String soundName, float volume, float pitch) {
        SoundEvent soundEvent = soundEvents.get(soundName);
        if (soundEvent != null) {
            playSound(soundEvent, volume, pitch);
        }
    }

    public static void playSoundLocally(String soundName) {
        playSoundLocally(soundName, 1.0f, 1.0f);
    }

    public static void playSoundLocally(String soundName, float volume, float pitch) {
        playSound(soundName, volume, pitch); // Same as playSound since we're using client sound manager
    }

    public static void playSound(SoundEvent soundEvent) {
        playSound(soundEvent, 1.0f, 1.0f);
    }

    public static void playSound(SoundEvent soundEvent, float volume, float pitch) {
        MinecraftClient client = MinecraftClient.getInstance();
        if (client.getSoundManager() != null) {
            client.getSoundManager().play(
                PositionedSoundInstance.master(soundEvent, pitch, volume)
            );
        }
    }

    public static void playToggleSound() {
        playSound(TOGGLE_SOUND);
    }
    public static void playDisableSound() { playSound(DISABLE_SOUND);}
    public static void playEnableSound() {
        playSound(ENABLE_SOUND);
    }
    public static void playNotifDefault() {
        playSound(NOTIFICATION_DEFAULT);
    }
    public static void playPingSound() {
        playSound(LOCATION_PING);
    }
    public static void playErrorSound() {
        playSound(ERROR_SOUND);
    }
    

    public static boolean isSoundRegistered(String soundName) {
        return soundEvents.containsKey(soundName);
    }
    public static SoundEvent getSoundEvent(String soundName) {
        return soundEvents.get(soundName);
    }
} 