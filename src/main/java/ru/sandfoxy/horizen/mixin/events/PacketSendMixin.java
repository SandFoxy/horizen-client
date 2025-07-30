package ru.sandfoxy.horizen.mixin.events;

import io.netty.channel.ChannelHandlerContext;
import net.minecraft.network.ClientConnection;
import net.minecraft.network.packet.Packet;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;
import ru.sandfoxy.horizen.events.PacketEvent;

@Mixin(ClientConnection.class)
public class PacketSendMixin {

    @Inject(method = "send(Lnet/minecraft/network/packet/Packet;)V", at = @At("HEAD"), cancellable = true)
    private void onPacketSend(Packet<?> packet, CallbackInfo ci) {
        PacketEvent.Send event = new PacketEvent.Send(packet);
        PacketEvent.SEND.invoker().onSend(event);
        
        if (event.isCancelled()) {
            ci.cancel();
        }
    }

    @Inject(method = "channelRead0(Lio/netty/channel/ChannelHandlerContext;Lnet/minecraft/network/packet/Packet;)V", at = @At("HEAD"), cancellable = true)
    private void onPacketReceive(ChannelHandlerContext channelHandlerContext, Packet<?> packet, CallbackInfo ci) {
        PacketEvent.Receive event = new PacketEvent.Receive(packet);
        PacketEvent.RECEIVE.invoker().onReceive(event);
        
        if (event.isCancelled()) {
            ci.cancel();
        }
    }
} 