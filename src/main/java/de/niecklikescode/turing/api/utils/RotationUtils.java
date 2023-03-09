package de.niecklikescode.turing.api.utils;

import net.minecraft.client.Minecraft;
import net.minecraft.client.entity.EntityPlayerSP;
import net.minecraft.entity.Entity;
import net.minecraft.util.MathHelper;

public class RotationUtils {

    private static EntityPlayerSP getLocalPlayer() {
        return Minecraft.getMinecraft().thePlayer;
    }

    public static float[] getRotation(Entity entity) {

        double deltaX = getLocalPlayer().posX - entity.posX;
        double deltaY = getLocalPlayer().posY - (entity.posY + entity.getEyeHeight());
        double deltaZ = getLocalPlayer().posZ - entity.posZ;

        double hypotenuse = MathHelper.sqrt_double(deltaX * deltaX + deltaZ * deltaZ);

        float yaw = (float)(MathHelper.atan2(deltaZ, deltaX) * 180.0 / 3.1415927410125732) - 90.0F;
        float pitch = (float)(-(MathHelper.atan2(deltaY, hypotenuse) * 180.0 / 3.1415927410125732));

        return new float[] { yaw, pitch };
    }

}
