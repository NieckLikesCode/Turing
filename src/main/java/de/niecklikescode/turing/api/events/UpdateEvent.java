package de.niecklikescode.turing.api.events;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.Setter;
import net.minecraftforge.fml.common.eventhandler.Event;

@AllArgsConstructor
public class UpdateEvent extends Event {

    @Setter
    @Getter
    private double x,y,z;

    @Getter
    @Setter
    private boolean onGround, isSneaking;

    @Getter
    @Setter
    private float yaw,pitch;

    @Getter
    private EventPhase eventPhase;

    public enum EventPhase {
        PRE,POST
    }

}
