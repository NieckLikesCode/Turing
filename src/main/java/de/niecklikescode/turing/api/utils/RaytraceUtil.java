package de.niecklikescode.turing.api.utils;

import net.minecraft.client.Minecraft;
import net.minecraft.util.*;

import static java.lang.Math.PI;
public class RaytraceUtil {

    private final static Minecraft mc = Minecraft.getMinecraft();

    /*
     * Taken from EntityRenderer, no need to reinvent the wheel
     */
    public static MovingObjectPosition getObjectMouseOver(float yaw, float pitch) {
        return getObjectMouseOver(yaw, pitch, mc.playerController.getBlockReachDistance());
    }

    public static MovingObjectPosition getObjectMouseOver(float yaw, float pitch, double range) {
        // Get players eye position
        Vec3 positionEyes = mc.thePlayer.getPositionEyes(0F);

        // Get look vector for passed rotations
        Vec3 playerLook = getVectorForRotation(yaw, pitch);
        // Extend look vector by range
        Vec3 direction = positionEyes.addVector(playerLook.xCoord * range, playerLook.yCoord * range, playerLook.zCoord * range);

        // Return raytraced block
        return mc.theWorld.rayTraceBlocks(positionEyes, direction, false, false, true);
    }

    private static Vec3 getVectorForRotation(float yaw, float pitch) {
        float f = MathHelper.cos(-yaw * (float)PI);
        float f1 = MathHelper.sin(-yaw * 0.017453292F - (float)PI);
        float f2 = -MathHelper.cos(-pitch * 0.017453292F);
        float f3 = MathHelper.sin(-pitch * 0.017453292F);
        return new Vec3(f1 * f2, f3, f * f2);
    }

}
