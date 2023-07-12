package de.niecklikescode.turing.modules;

import de.niecklikescode.turing.api.events.UpdateEvent;
import de.niecklikescode.turing.api.modules.Info;
import de.niecklikescode.turing.api.modules.Module;
import net.minecraft.potion.Potion;
import net.minecraftforge.fml.common.eventhandler.SubscribeEvent;

@Info(category = Module.Category.MOVEMENT, description = "Makes you sprint whenever possible")
public class Sprint extends Module {

    @SubscribeEvent
    public void onPreUpdate(UpdateEvent event) {

        boolean isSaturatedOrFlying = (float) getLocalPlayer().getFoodStats().getFoodLevel() > 6.0F || getLocalPlayer().capabilities.allowFlying;

        boolean canSprint = getLocalPlayer().onGround
                && !getLocalPlayer().movementInput.sneak
                && getLocalPlayer().movementInput.moveForward >= 0.8F
                && !getLocalPlayer().isSprinting()
                && isSaturatedOrFlying
                && !getLocalPlayer().isUsingItem()
                && !getLocalPlayer().isPotionActive(Potion.blindness);

        if (canSprint) MC.thePlayer.setSprinting(true);
    }

}
