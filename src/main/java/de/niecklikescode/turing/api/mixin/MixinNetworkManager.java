package de.niecklikescode.turing.api.mixin;

import de.niecklikescode.turing.api.events.PacketEvent;
import io.netty.channel.ChannelHandlerContext;
import net.minecraft.network.NetworkManager;
import net.minecraft.network.Packet;
import net.minecraftforge.common.MinecraftForge;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

@Mixin(NetworkManager.class)
public class MixinNetworkManager {

    @Inject(method = "channelRead0(Lio/netty/channel/ChannelHandlerContext;Lnet/minecraft/network/Packet;)V", at = @At(value = "INVOKE", target = "Lnet/minecraft/network/Packet;processPacket(Lnet/minecraft/network/INetHandler;)V"), cancellable = true)
    public void channelRead0(ChannelHandlerContext channelHandlerContext, Packet<?> packet, CallbackInfo ci) {
        PacketEvent readEvent = new PacketEvent(PacketEvent.EventPhase.READ, packet);
        MinecraftForge.EVENT_BUS.post(readEvent);

        if(readEvent.isCanceled()) ci.cancel();
    }

    @Inject(method = "sendPacket(Lnet/minecraft/network/Packet;)V", at=@At(value = "INVOKE", target = "Lnet/minecraft/network/NetworkManager;dispatchPacket(Lnet/minecraft/network/Packet;[Lio/netty/util/concurrent/GenericFutureListener;)V"), cancellable = true)
    public void sendPacket(Packet<?> packet, CallbackInfo ci) {
        PacketEvent writeEvent = new PacketEvent(PacketEvent.EventPhase.WRITE, packet);
        MinecraftForge.EVENT_BUS.post(writeEvent);

        if(writeEvent.isCanceled()) ci.cancel();
    }


}
