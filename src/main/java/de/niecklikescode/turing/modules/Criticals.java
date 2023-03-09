package de.niecklikescode.turing.modules;

import de.niecklikescode.turing.api.events.MouseClickEvent;
import de.niecklikescode.turing.api.events.TickEvent;
import de.niecklikescode.turing.api.main.Turing;
import de.niecklikescode.turing.api.modules.Info;
import de.niecklikescode.turing.api.modules.Module;
import net.minecraft.entity.Entity;
import net.minecraft.network.play.client.C02PacketUseEntity;
import net.minecraft.potion.Potion;
import net.minecraft.util.MovingObjectPosition;
import net.minecraftforge.fml.common.eventhandler.SubscribeEvent;
import org.lwjgl.input.Keyboard;

@Info(description = "Delays your hits to make them critical hits", category = Module.Category.COMBAT, keyBind = Keyboard.KEY_R)
public class Criticals extends Module {

    /*
     * Since just spoofing position and movement packets is (honestly boring and) easy to detect we're going to aim
     * for a different approach
     *
     * To crit in Minecraft you'll have to follow 7 conditions
     *  1. The player's fall distance has to be greater than 0
     *  2. The player must be airborne
     *  3. The player can't be on a ladder
     *  4. The player can't have the blindness effect (fun fact! :^) )
     *  5. The player can't ride an entity
     *
     * Since were mostly unable to spoof conditions 2-5 it leaves us with checking if the player is falling.
     * Exploiting this mechanic is as easy as extrapolating the local players vertical position and in case he is moving upwards
     * delaying our hit till the condition for a critical hit is met.
     */

    // Calculates the next falling tick assuming the player is following his current trajectory and returns true
    // when there is a falling tick available
    private boolean extrapolateFutureTicks(final int maxTicks) {

        double origin = getLocalPlayer().posY;
        double prevY = getLocalPlayer().prevPosY;

        boolean shouldCalc = getLocalPlayer().fallDistance == 0.0F
                && !getLocalPlayer().onGround
                && !getLocalPlayer().isOnLadder()
                && !getLocalPlayer().isInWater()
                && !getLocalPlayer().isPotionActive(Potion.blindness)
                && getLocalPlayer().ridingEntity == null;

        if (!shouldCalc) return false;

        // Calculate as many future ticks as specified
        for (int tick = 1; tick < maxTicks; tick++) {

            // Calculating the next y coordinate using Minecrafts gravity constant (can be found in EntityLivingBase:moveEntityWithHeading)
            double nextY = origin + (((origin - prevY) - 0.08) * 0.9800000190734863);

            // The player will move down in the next tick
            if ((nextY - origin) < 0)
                return true;

            prevY = origin;
            origin = nextY;
        }

        // No downward motion will happen in the next X ticks
        return false;
    }

    @SubscribeEvent
    public void onTick(TickEvent event) {
        if (!awaitHit) return;

        if (getLocalPlayer().fallDistance > 0.1) {
            awaitHit = false;
            Entity mouseOver = MC.objectMouseOver.entityHit;

            if (mouseOver != null) {

                getLocalPlayer().swingItem();
                getLocalPlayer().sendQueue.addToSendQueue(new C02PacketUseEntity(mouseOver, C02PacketUseEntity.Action.ATTACK));

            }
        }
    }

    private boolean awaitHit;

    @SubscribeEvent
    public void onAttack(MouseClickEvent event) {

        if (MC.objectMouseOver == null
                || MC.objectMouseOver.typeOfHit != MovingObjectPosition.MovingObjectType.ENTITY) return;

        boolean willCrit = getLocalPlayer().fallDistance > 0.0F
                && !getLocalPlayer().onGround
                && !getLocalPlayer().isOnLadder()
                && !getLocalPlayer().isInWater()
                && !getLocalPlayer().isPotionActive(Potion.blindness)
                && getLocalPlayer().ridingEntity == null;

        if (!willCrit && extrapolateFutureTicks(10)) {
            Turing.getLogger().info("Missed critical hit!");
            event.setCanceled(true);

            awaitHit = true;
        }

    }


}
