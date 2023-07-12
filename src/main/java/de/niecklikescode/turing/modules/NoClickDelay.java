package de.niecklikescode.turing.modules;

import de.niecklikescode.turing.api.events.UpdateEvent;
import de.niecklikescode.turing.api.modules.Info;
import de.niecklikescode.turing.api.modules.Module;
import net.minecraftforge.fml.common.eventhandler.SubscribeEvent;
import org.lwjgl.input.Keyboard;

import static de.niecklikescode.turing.api.utils.PlayerUtils.addClientMessage;
import static de.niecklikescode.turing.api.utils.ReflectionUtils.setField;

@Info(name = "No Click Delay", description = "Removes click delay when hitting the air", category = Module.Category.MISC, keyBind = Keyboard.KEY_J)
public class NoClickDelay extends Module {

    @SubscribeEvent
    public void onUpdate(UpdateEvent event) {
        if(event.getEventPhase() != UpdateEvent.EventPhase.PRE) return;
        try {
            setField(MC, 0, "leftClickCounter", "ag");
        } catch (IllegalAccessException e) {
            addClientMessage("Unable to access 'leftClickCounter/ag'! Disabling mod.");
            toggle();
            throw new RuntimeException(e);
        }
    }

}
