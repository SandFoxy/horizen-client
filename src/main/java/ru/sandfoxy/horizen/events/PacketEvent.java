package ru.sandfoxy.horizen.events;

import net.fabricmc.fabric.api.event.Event;
import net.fabricmc.fabric.api.event.EventFactory;
import net.minecraft.network.packet.Packet;

public class PacketEvent {
    public static final Event<ReceiveCallback> RECEIVE = EventFactory.createArrayBacked(ReceiveCallback.class,
            (listeners) -> (event) -> {
                for (ReceiveCallback listener : listeners) {
                    listener.onReceive(event);
                    if (event.isCancelled()) {
                        break;
                    }
                }
            });

    public static final Event<SendCallback> SEND = EventFactory.createArrayBacked(SendCallback.class,
            (listeners) -> (event) -> {
                for (SendCallback listener : listeners) {
                    listener.onSend(event);
                    if (event.isCancelled()) {
                        break;
                    }
                }
            });

    @FunctionalInterface
    public interface ReceiveCallback {
        void onReceive(Receive event);
    }

    @FunctionalInterface
    public interface SendCallback {
        void onSend(Send event);
    }

    public static class Receive {
        private final Packet<?> packet;
        private boolean cancelled = false;

        public Receive(Packet<?> packet) {
            this.packet = packet;
        }

        public Packet<?> getPacket() {
            return packet;
        }

        public void cancel() {
            this.cancelled = true;
        }

        public boolean isCancelled() {
            return cancelled;
        }
    }

    public static class Send {
        private final Packet<?> packet;
        private boolean cancelled = false;

        public Send(Packet<?> packet) {
            this.packet = packet;
        }

        public Packet<?> getPacket() {
            return packet;
        }

        public void cancel() {
            this.cancelled = true;
        }

        public boolean isCancelled() {
            return cancelled;
        }
    }
} 