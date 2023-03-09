package de.niecklikescode.turing.api.mixin;

import de.niecklikescode.turing.api.events.UpdateEvent;
import net.minecraft.client.Minecraft;
import net.minecraft.client.entity.EntityPlayerSP;
import net.minecraft.client.network.NetHandlerPlayClient;
import net.minecraft.network.play.client.C03PacketPlayer;
import net.minecraft.network.play.client.C0BPacketEntityAction;
import net.minecraftforge.common.MinecraftForge;
import org.spongepowered.asm.mixin.Final;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Overwrite;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

@Mixin(EntityPlayerSP.class)
public class MixinEntityPlayerSP {

    private final Minecraft MINECRAFT = Minecraft.getMinecraft();

    @Shadow
    private boolean serverSprintState;

    @Shadow
    private boolean serverSneakState;

    @Final
    @Shadow
    public NetHandlerPlayClient sendQueue;

    @Shadow
    private double lastReportedPosX, lastReportedPosY, lastReportedPosZ;

    @Shadow
    private float lastReportedYaw, lastReportedPitch;

    @Shadow
    private int positionUpdateTicks;

    /**
     * @author Teichkoenig
     * @reason Overwriting to call traditionally used UpdateEvent and enable rotation
     * manipulation and ground/position spoofing
     */
    @Overwrite
    public void onUpdateWalkingPlayer() {

        EntityPlayerSP localPlayer = MINECRAFT.thePlayer;

        UpdateEvent preUpdate = new UpdateEvent(
                localPlayer.posX,
                localPlayer.posY,
                localPlayer.posZ,
                localPlayer.onGround,
                localPlayer.isSneaking(),
                localPlayer.rotationYaw,
                localPlayer.rotationPitch,
                UpdateEvent.EventPhase.PRE
        );

        MinecraftForge.EVENT_BUS.post(preUpdate);

        localPlayer.posX = preUpdate.getX();
        localPlayer.posY = preUpdate.getY();
        localPlayer.posZ = preUpdate.getZ();

        localPlayer.onGround = preUpdate.isOnGround();
        localPlayer.setSneaking(preUpdate.isSneaking());

        localPlayer.rotationYaw = preUpdate.getYaw();
        localPlayer.rotationPitch = preUpdate.getPitch();

        /*
         * Synchronize client and server sprint-state
         */
        boolean isSprinting = localPlayer.isSprinting();
        if (isSprinting != this.serverSprintState) {
            if (isSprinting) {
                this.sendQueue.addToSendQueue(new C0BPacketEntityAction(localPlayer, C0BPacketEntityAction.Action.START_SPRINTING));
            } else {
                this.sendQueue.addToSendQueue(new C0BPacketEntityAction(localPlayer, C0BPacketEntityAction.Action.STOP_SPRINTING));
            }

            this.serverSprintState = isSprinting;
        }

        /*
         * Synchronize client and server sneak-state
         */
        boolean isSneaking = localPlayer.isSneaking();

        if (isSneaking != this.serverSneakState) {
            if (isSneaking) {
                this.sendQueue.addToSendQueue(new C0BPacketEntityAction(localPlayer, C0BPacketEntityAction.Action.START_SNEAKING));
            } else {
                this.sendQueue.addToSendQueue(new C0BPacketEntityAction(localPlayer, C0BPacketEntityAction.Action.STOP_SNEAKING));
            }

            this.serverSneakState = isSneaking;
        }

        if (MINECRAFT.getRenderViewEntity() == localPlayer) {

            double deltaX = localPlayer.posX - this.lastReportedPosX;
            double deltaY = localPlayer.getEntityBoundingBox().minY - this.lastReportedPosY;
            double deltaZ = localPlayer.posZ - this.lastReportedPosZ;

            double deltaYaw = localPlayer.rotationYaw - this.lastReportedYaw;
            double deltaPitch = localPlayer.rotationPitch - this.lastReportedPitch;

            boolean hasMoved = deltaX * deltaX + deltaY * deltaY + deltaZ * deltaZ > 9.0E-4 || this.positionUpdateTicks >= 20;
            boolean hasRotated = deltaYaw != 0.0 || deltaPitch != 0.0;

            if (localPlayer.ridingEntity == null) {
                if (hasMoved && hasRotated) {
                    this.sendQueue.addToSendQueue(new C03PacketPlayer.C06PacketPlayerPosLook(localPlayer.posX, localPlayer.getEntityBoundingBox().minY, localPlayer.posZ, localPlayer.rotationYaw, localPlayer.rotationPitch, localPlayer.onGround));
                } else if (hasMoved) {
                    this.sendQueue.addToSendQueue(new C03PacketPlayer.C04PacketPlayerPosition(localPlayer.posX, localPlayer.getEntityBoundingBox().minY, localPlayer.posZ, localPlayer.onGround));
                } else if (hasRotated) {
                    this.sendQueue.addToSendQueue(new C03PacketPlayer.C05PacketPlayerLook(localPlayer.rotationYaw, localPlayer.rotationPitch, localPlayer.onGround));
                } else {
                    this.sendQueue.addToSendQueue(new C03PacketPlayer(localPlayer.onGround));
                }
            } else {
                this.sendQueue.addToSendQueue(new C03PacketPlayer.C06PacketPlayerPosLook(localPlayer.motionX, -999.0, localPlayer.motionZ, localPlayer.rotationYaw, localPlayer.rotationPitch, localPlayer.onGround));
                hasMoved = false;
            }

            ++this.positionUpdateTicks;
            if (hasMoved) {
                this.lastReportedPosX = localPlayer.posX;
                this.lastReportedPosY = localPlayer.getEntityBoundingBox().minY;
                this.lastReportedPosZ = localPlayer.posZ;
                this.positionUpdateTicks = 0;
            }

            if (hasRotated) {
                this.lastReportedYaw = localPlayer.rotationYaw;
                this.lastReportedPitch = localPlayer.rotationPitch;
            }
        }

    }

    @Inject(method = {"onUpdate"}, at = {@At("TAIL")})
    public void onUpdate(CallbackInfo ci) {
        EntityPlayerSP localPlayer = MINECRAFT.thePlayer;

        MinecraftForge.EVENT_BUS.post(new UpdateEvent(
                localPlayer.posX,
                localPlayer.posY,
                localPlayer.posZ,
                localPlayer.onGround,
                localPlayer.isSneaking(),
                localPlayer.rotationYaw,
                localPlayer.rotationPitch,
                UpdateEvent.EventPhase.POST
        ));

    }


}
