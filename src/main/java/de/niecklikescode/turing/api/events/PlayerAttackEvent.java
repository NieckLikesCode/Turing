package de.niecklikescode.turing.api.events;

import lombok.AllArgsConstructor;
import lombok.Getter;
import net.minecraft.entity.Entity;
import net.minecraftforge.fml.common.eventhandler.Cancelable;
import net.minecraftforge.fml.common.eventhandler.Event;

@Cancelable
@AllArgsConstructor
public class PlayerAttackEvent extends Event {

    @Getter
    private Entity target;

    @Getter
    private EventPhase eventPhase;

    public enum EventPhase {
        PRE,POST
    }

}
