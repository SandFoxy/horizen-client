package ru.sandfoxy.horizen.events;

import net.fabricmc.fabric.api.event.Event;
import net.fabricmc.fabric.api.event.EventFactory;
import net.minecraft.client.sound.SoundInstance;

public interface OnCheatCommandMessage {
    Event<OnCheatCommandMessage> EVENT = EventFactory.createArrayBacked(OnCheatCommandMessage.class,
            (listeners) -> (String message, boolean toHud) -> {
                for (OnCheatCommandMessage listener : listeners) {
                    listener.sendMessage(message,toHud);
                }
            });

    void sendMessage(String message, boolean toHud);
}