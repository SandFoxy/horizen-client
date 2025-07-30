package ru.sandfoxy.horizen.events;

import net.fabricmc.fabric.api.event.Event;
import net.fabricmc.fabric.api.event.EventFactory;
import net.minecraft.client.sound.SoundInstance;

public interface OnSoundPlayedEvent {
    Event<OnSoundPlayedEvent> EVENT = EventFactory.createArrayBacked(OnSoundPlayedEvent.class,
            (listeners) -> (SoundInstance sound) -> {
                for (OnSoundPlayedEvent listener : listeners) {
                    listener.playSound(sound);
                }
            });

    void playSound(SoundInstance sound);
}