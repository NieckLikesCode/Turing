package de.niecklikescode.turing.modules;

import de.niecklikescode.turing.api.events.UpdateEvent;
import de.niecklikescode.turing.api.modules.Info;
import de.niecklikescode.turing.api.modules.Module;
import de.niecklikescode.turing.api.utils.PlayerUtils;
import de.niecklikescode.turing.api.utils.ReflectionUtils;
import net.minecraftforge.fml.common.eventhandler.SubscribeEvent;
import org.lwjgl.input.Keyboard;

@Info(description = "Removes/Modifies the right click delay", category = Module.Category.PLAYER, keyBind = Keyboard.KEY_J)
public class FastPlace extends Module {

    @SubscribeEvent
    public void onUpdate(UpdateEvent event) {
        if(event.getEventPhase() != UpdateEvent.EventPhase.PRE) return;
        try {
            ReflectionUtils.setField(MC, 0, "rightClickDelayTimer", "ap");
        } catch (IllegalAccessException e) {
            PlayerUtils.addClientMessage("Unable to access field 'rightClickDelayTimer/ap'! Disabling mod.");
            toggle();
        }
    }

}
