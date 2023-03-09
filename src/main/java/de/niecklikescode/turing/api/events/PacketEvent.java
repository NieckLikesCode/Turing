package de.niecklikescode.turing.api.events;

import lombok.AllArgsConstructor;
import lombok.Getter;
import net.minecraft.network.Packet;
import net.minecraftforge.fml.common.eventhandler.Cancelable;
import net.minecraftforge.fml.common.eventhandler.Event;

@Cancelable
@AllArgsConstructor
public class PacketEvent extends Event {

    @Getter
    private EventPhase eventPhase;

    @Getter
    private Packet<?> packet;

    public enum EventPhase {
        READ,WRITE
    }

}
