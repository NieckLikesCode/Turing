package de.niecklikescode.turing.api.utils;

import net.minecraft.block.state.IBlockState;
import net.minecraft.client.Minecraft;
import net.minecraft.init.Blocks;
import net.minecraft.inventory.Container;
import net.minecraft.inventory.Slot;
import net.minecraft.item.ItemStack;
import net.minecraft.util.BlockPos;
import net.minecraft.util.ChatComponentText;
import net.minecraft.util.MathHelper;
import net.minecraft.util.Vec3;

import java.util.ArrayList;
import java.util.List;
import java.util.function.Predicate;

public class PlayerUtils {

    private static final Minecraft mc = Minecraft.getMinecraft();

    public static Vec3 extrapolate(Vec3 current, Vec3 last, float ticks) {
        double x = current.xCoord + (current.xCoord - last.xCoord) * ticks;
        double y = current.yCoord + (current.yCoord - last.yCoord) * ticks;
        double z = current.zCoord + (current.zCoord - last.zCoord) * ticks;
        return new Vec3(x, y, z);
    }

    public static Vec3 getLastPosition() {
        return new Vec3(mc.thePlayer.lastTickPosX, mc.thePlayer.lastTickPosY, mc.thePlayer.lastTickPosZ);
    }

    public static double predictFallDamage() {
        if(mc.thePlayer.onGround) return 0;

        BlockPos groundPosition = new BlockPos(mc.thePlayer).down();

        while(mc.theWorld.getBlockState(groundPosition).getBlock() == Blocks.air)
            groundPosition = groundPosition.down();

        // When falling on the slab or fence you're not falling an entire black, so we rather use the max bounding box position
        // to correctly calculate the end position
        double groundY = mc.theWorld.getBlockState(groundPosition).getBlock().getSelectedBoundingBox(mc.theWorld, groundPosition).maxY;

        return mc.thePlayer.posY - groundY;
    }

    // TODO: Prediction seems to mess up sometimes but I'm to lazy too debug it rn
    public static Vec3 calculateNextGround(int maxTicks) {
        // Man I'd sure love to just use BlockPos, but it always rounds to integers which is impractical as hell for our use-case
        Vec3 lastPosition = new Vec3(mc.thePlayer.prevPosX, mc.thePlayer.prevPosY, mc.thePlayer.prevPosZ);
        Vec3 currentPosition = new Vec3(mc.thePlayer.posX, mc.thePlayer.posY, mc.thePlayer.posZ);

        // Abort calculations if the player is already on Ground
        if(mc.thePlayer.onGround) return currentPosition;

        for(int tick = 0; tick < maxTicks; tick++) {
            // Forward and strafing values are multiplied by 0,98 before being used in moveEntityWithHeading (see EntityLivingBase.class:onLivingUpdate)
            float strafe = mc.thePlayer.moveStrafing * 0.98F,
                    forward = mc.thePlayer.moveForward * 0.98F,
                    friction = mc.thePlayer.jumpMovementFactor; // jumpMovementFactor equals friction when airborne

            // We substitute motion values for coordinate differences, since they store the same information, and we've been working with coordinates the entire time
            double diffX = lastPosition.xCoord - currentPosition.xCoord, diffZ = lastPosition.zCoord - currentPosition.zCoord;

            // Code ripped straight from Entity.class:moveFlying; only the variables are renamed to make the code more readable
            float distanceMoved = strafe * strafe + forward * forward;
            if (distanceMoved >= 1.0E-4F) {
                distanceMoved = MathHelper.sqrt_float(distanceMoved);
                if (distanceMoved < 1.0F) {
                    distanceMoved = 1.0F;
                }

                distanceMoved = friction / distanceMoved;
                strafe *= distanceMoved;
                forward *= distanceMoved;

                float f1 = MathHelper.sin(mc.thePlayer.rotationYaw * (float)Math.PI / 180.0F);
                float f2 = MathHelper.cos(mc.thePlayer.rotationYaw * (float)Math.PI / 180.0F);

                // Actually apply the entity's movement
                diffX += (double) (strafe * f2 - forward * f1);
                diffZ += (double) (forward * f2 + strafe * f1);
            }

                // Apply the differential change on the current coordinates
                double nextX = currentPosition.xCoord + diffX;
                double nextZ = currentPosition.zCoord + diffZ;

                // Since the players can not control the vertical movement once airborne the calculation is significantly easier
                double nextY = currentPosition.yCoord + ((currentPosition.yCoord - lastPosition.yCoord) - 0.08) * 0.9800000190734863;

                // Update current and last position to calculate the next position on the next iteration
                lastPosition = currentPosition;
                currentPosition = new Vec3(nextX, nextY, nextZ);

                // Stop the calculations once we found a ground position
                IBlockState blockState = mc.theWorld.getBlockState(new BlockPos(currentPosition));

                if(blockState.getBlock().getCollisionBoundingBox(mc.theWorld, new BlockPos(currentPosition), blockState) != null)
                    return currentPosition;

        }
        // No ground position could be found in the next X ticks
        return null;
    }

    public static boolean isHotbarSlot(int slot) {
        return (slot >= 36 && slot <= 44);
    }

    public static int slotToHotbar(int slot) {
        return isHotbarSlot(slot) ? isHotbarSlot(slot) ? slot - 36 : -1 : -1;
    }

    public static ArrayList<Slot> getItemSlots(Predicate<ItemStack> filter) {
        ArrayList<Slot> slots = new ArrayList<>();
        Container inventory = mc.thePlayer.inventoryContainer;

        for (Slot slot : inventory.inventorySlots) {
            ItemStack stack = slot.getStack();

            if(stack == null) continue;

            if(filter.test(stack))
                slots.add(slot);
        }

        return slots;
    }

    public static ArrayList<Slot> getItemSlots(List<Slot> slots, Predicate<ItemStack> filter) {
        ArrayList<Slot> validSlots = new ArrayList<>();

        for (Slot slot : slots) {
            ItemStack stack = slot.getStack();

            if(stack == null) continue;

            if(filter.test(stack))
                validSlots.add(slot);
        }

        return validSlots;
    }

    public static void addClientMessage(String message) {
        mc.thePlayer.addChatMessage(new ChatComponentText(String.format("§l§cT§furing §r%s", message)));
    }

    public static void addChatMessage(String message) {
        mc.thePlayer.addChatMessage(new ChatComponentText(message));
    }


}
