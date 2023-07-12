package de.niecklikescode.turing.api.utils;

import net.minecraft.client.Minecraft;
import net.minecraft.client.entity.EntityPlayerSP;
import net.minecraft.entity.Entity;
import net.minecraft.util.MathHelper;
import net.minecraft.util.Vec3;

public class RotationUtils {

    private static EntityPlayerSP getLocalPlayer() {
        return Minecraft.getMinecraft().thePlayer;
    }

    public static float[] getRotation(Entity entity) {

        double deltaX = getLocalPlayer().posX - entity.posX;
        double deltaY = getLocalPlayer().posY - (entity.posY + entity.getEyeHeight());
        double deltaZ = getLocalPlayer().posZ - entity.posZ;

        double dist = MathHelper.sqrt_double(deltaX * deltaX + deltaZ * deltaZ);

        float yaw = (float) (MathHelper.atan2(deltaZ, deltaX) * 180.0 / 3.1415927410125732) - 90.0F;
        float pitch = (float) (-(MathHelper.atan2(deltaY, dist) * 180.0 / 3.1415927410125732));

        return new float[]{yaw, pitch};
    }

    public static float[] rotationsToVector(Vec3 paramVec3) {
        Vec3 localVec31 = Minecraft.getMinecraft().thePlayer.getPositionEyes(1.0F);
        Vec3 localVec32 = paramVec3.subtract(localVec31);
        return new float[]{(float) Math.toDegrees(Math.atan2(localVec32.zCoord, localVec32.xCoord)) - 90.0F, (float) -Math.toDegrees(Math.atan2(localVec32.yCoord, Math.hypot(localVec32.xCoord, localVec32.zCoord)))};
    }



}
