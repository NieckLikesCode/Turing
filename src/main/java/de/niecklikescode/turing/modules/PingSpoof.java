package de.niecklikescode.turing.modules;

import de.niecklikescode.turing.api.events.PacketEvent;
import de.niecklikescode.turing.api.events.UpdateEvent;
import de.niecklikescode.turing.api.modules.Info;
import de.niecklikescode.turing.api.modules.Module;
import net.minecraft.network.Packet;
import net.minecraft.network.play.client.C00PacketKeepAlive;
import net.minecraft.network.play.client.C16PacketClientStatus;
import net.minecraftforge.fml.common.eventhandler.SubscribeEvent;
import org.lwjgl.input.Keyboard;

import java.util.*;

@Info(name = "Ping Spoof", description = "Spoofs your ping to exploit lag compensation and get less flags", category = Module.Category.MISC, keyBind = Keyboard.KEY_Z)
public class PingSpoof extends Module {

    private static final HashMap<Packet<?>, Long> packetQueue = new HashMap<>();

    @SubscribeEvent
    public void onPacket(PacketEvent event) {
        Packet<?> packet = event.getPacket();

        if( (packet instanceof C00PacketKeepAlive
            || packet instanceof C16PacketClientStatus)
            && !packetQueue.containsKey(packet)) {
            event.setCanceled(true);
            // Safe the packet alongside the current system time to later determine the artificial delay
            packetQueue.put(packet, System.currentTimeMillis());
        }

    }

    // Maximum amount of time "ping" packets should be delayed by (in milliseconds)
    private final int PING = 300;

    // TODO: Improve the syntax
    @SubscribeEvent
    public void onUpdate(UpdateEvent event) {
        if(event.getEventPhase() != UpdateEvent.EventPhase.PRE) return;

        // Filter packets first then send and remove them to prevent a concurrency exception
        List<Packet<?>> queuedPackets = new ArrayList<>();

        packetQueue.forEach((packet, ms) -> {
            if(System.currentTimeMillis() >= ms + PING) queuedPackets.add(packet);
        });

        queuedPackets.forEach(PingSpoof::sendQueuedPacket);
    }

    private static void sendQueuedPacket(Packet<?> packet) {
        getLocalPlayer().sendQueue.addToSendQueue(packet);
        packetQueue.remove(packet);
    }

    @Override
    public void disableMod() {
        packetQueue.clear();
    }
}
