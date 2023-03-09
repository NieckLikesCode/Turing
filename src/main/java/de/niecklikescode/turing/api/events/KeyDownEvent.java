package de.niecklikescode.turing.api.events;

import lombok.AllArgsConstructor;
import lombok.Getter;
import net.minecraftforge.fml.common.eventhandler.Event;

@AllArgsConstructor
public class KeyDownEvent extends Event {

    @Getter
    private int keyCode;

}
