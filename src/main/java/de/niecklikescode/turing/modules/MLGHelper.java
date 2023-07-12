package de.niecklikescode.turing.modules;

import de.niecklikescode.turing.api.events.UpdateEvent;
import de.niecklikescode.turing.api.modules.Info;
import de.niecklikescode.turing.api.modules.Module;
import static de.niecklikescode.turing.api.utils.PlayerUtils.*;

import de.niecklikescode.turing.api.utils.RaytraceUtil;
import de.niecklikescode.turing.api.utils.RotationUtils;
import net.minecraft.enchantment.Enchantment;
import net.minecraft.enchantment.EnchantmentHelper;
import net.minecraft.init.Blocks;
import net.minecraft.inventory.Slot;
import net.minecraft.item.ItemStack;
import net.minecraft.network.play.client.C08PacketPlayerBlockPlacement;
import net.minecraft.network.play.client.C09PacketHeldItemChange;
import net.minecraft.network.play.client.C0APacketAnimation;
import net.minecraft.util.BlockPos;
import net.minecraft.util.MovingObjectPosition;
import net.minecraft.util.Vec3;
import net.minecraftforge.fml.common.eventhandler.SubscribeEvent;
import org.lwjgl.input.Keyboard;

import java.util.ArrayList;

@Info(name = "MLG Helper", description = "Uses certain items to negate fall damage", category = Module.Category.VISUAL, keyBind = Keyboard.KEY_V)
public class MLGHelper extends Module {

    // TODO: Because I seem to have ruined the code at some point and nothing works anymore, I have to clean up the mess at some point
    @SubscribeEvent
    public void onUpdate(UpdateEvent event) {

        double fallDamage = calcFallDamage();
        boolean isDamageLethal = getLocalPlayer().getHealth() <= fallDamage;

        if(getLocalPlayer().motionY >= 0 || getLocalPlayer().onGround) return; // Check if the player is falling
        if(!isDamageLethal && getLocalPlayer().fallDistance < 3) return; // Check whether the fall is lethal or the player is falling for a set amount of blocks already
        if(getWorld().getBlockState(new BlockPos(getLocalPlayer())).getBlock() == Blocks.web) return; // Player already is in web

        Vec3 nextGround = calculateNextGround(5);
        if(nextGround == null || getWorld().getBlockState(new BlockPos(nextGround)).getBlock() == Blocks.air) return;

        int cobweb = getCobwebSlot();

        // The player does not have a cobweb in his inventory
        if(cobweb == -1) return;

        // TODO: Implement a method to take cobwebs out of the inventory when there are none in the hotbar
        // If the cobweb is in the hotbar, select it
        if(!isHotbarSlot(cobweb)) return;

        BlockPos aimLocation = new BlockPos(nextGround);

        float[] rotations = RotationUtils.rotationsToVector(nextGround);
        MovingObjectPosition objectPosition = RaytraceUtil.getObjectMouseOver(rotations[0], rotations[1]);

        if(event.getEventPhase() == UpdateEvent.EventPhase.PRE) {
            event.setYaw(rotations[0]);
            event.setPitch(rotations[1]);
        } else {
            float x = (float)(objectPosition.hitVec.xCoord - (double)objectPosition.getBlockPos().getX());
            float y = (float)(objectPosition.hitVec.yCoord - (double)objectPosition.getBlockPos().getY());
            float z = (float)(objectPosition.hitVec.zCoord - (double)objectPosition.getBlockPos().getZ());

            MC.getNetHandler().addToSendQueue(new C09PacketHeldItemChange(slotToHotbar(cobweb)));
            MC.getNetHandler().addToSendQueue(new C08PacketPlayerBlockPlacement(aimLocation, objectPosition.sideHit.getIndex(), getLocalPlayer().inventory.getStackInSlot(cobweb), x, y, z));
            MC.getNetHandler().addToSendQueue(new C0APacketAnimation());
            MC.getNetHandler().addToSendQueue(new C09PacketHeldItemChange(getLocalPlayer().inventory.currentItem));

            toggle();
        }

    }

    private int getCobwebSlot() {
        // Get an array with all inventory slots containing cobwebs
        ArrayList<Slot> cobwebSlots = getItemSlots(stack -> stack.getItem().getRegistryName().equals("minecraft:web"));

        // No Cobweb in inventory
        if(cobwebSlots.isEmpty()) return -1;
        // Hotbar slots are be preferred since taking it out of the inventory could in theory cause flags
        else {
            for (Slot cobwebSlot : cobwebSlots) {
                if(isHotbarSlot(cobwebSlot.getSlotIndex())) return cobwebSlot.getSlotIndex();
            }
            return cobwebSlots.get(0).getSlotIndex();
        }
    }

    // TODO: Add protection enchantment into damage calculations
    private double calcFallDamage() {
        ItemStack boots = getLocalPlayer().getCurrentArmor(0);
        int featherFalling = EnchantmentHelper.getEnchantmentLevel(Enchantment.featherFalling.effectId, boots);

        double fallDamage = (getLocalPlayer().fallDistance -3) / 2;

        return getLocalPlayer().fallDistance > 0 ? fallDamage * (1-featherFalling) : 0;
    }

}
