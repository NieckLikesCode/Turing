package de.niecklikescode.turing.modules;

import de.niecklikescode.turing.api.events.UpdateEvent;
import de.niecklikescode.turing.api.modules.Info;
import de.niecklikescode.turing.api.modules.Module;
import de.niecklikescode.turing.api.utils.ReflectionUtils;
import net.minecraftforge.fml.common.eventhandler.SubscribeEvent;
import org.lwjgl.input.Keyboard;
import static de.niecklikescode.turing.api.utils.PlayerUtils.addClientMessage;

@Info(name = "No Jump Delay", description = "Automatically spam jump by holding down the jump key", category = Module.Category.MOVEMENT, keyBind = Keyboard.KEY_N)
public class NoJumpDelay extends Module {

    @SubscribeEvent
    public void onUpdate(UpdateEvent event) {
        if(event.getEventPhase() != UpdateEvent.EventPhase.PRE) return;
        resetJumpTimer();
    }

    private void resetJumpTimer() {
        try {
            ReflectionUtils.setField(getLocalPlayer(), 0, "jumpTicks", "bn");
        } catch (IllegalAccessException e) {
            addClientMessage("Unable to set field 'jumpTicks/bn'! Disabling mod.");
            toggle();
            throw new RuntimeException(e);
        }

    }

}
