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
        boolean isSneaking = preUpdate.isSneaking();

        if (isSneaking != this.serverSneakState) {
            if (isSneaking) {
                this.sendQueue.addToSendQueue(new C0BPacketEntityAction(localPlayer, C0BPacketEntityAction.Action.START_SNEAKING));
            } else {
                this.sendQueue.addToSendQueue(new C0BPacketEntityAction(localPlayer, C0BPacketEntityAction.Action.STOP_SNEAKING));
            }

            this.serverSneakState = isSneaking;
        }

        if (MINECRAFT.getRenderViewEntity() == localPlayer) {

            double deltaX = preUpdate.getX() - this.lastReportedPosX;
            double deltaY = preUpdate.getY() - this.lastReportedPosY;
            double deltaZ = preUpdate.getZ() - this.lastReportedPosZ;

            double deltaYaw = preUpdate.getYaw() - this.lastReportedYaw;
            double deltaPitch = preUpdate.getPitch() - this.lastReportedPitch;

            boolean hasMoved = deltaX * deltaX + deltaY * deltaY + deltaZ * deltaZ > 9.0E-4 || this.positionUpdateTicks >= 20;
            boolean hasRotated = deltaYaw != 0.0 || deltaPitch != 0.0;

            if (localPlayer.ridingEntity == null) {
                if (hasMoved && hasRotated) {
                    this.sendQueue.addToSendQueue(new C03PacketPlayer.C06PacketPlayerPosLook(preUpdate.getX(), preUpdate.getY(), preUpdate.getZ(), preUpdate.getYaw(), preUpdate.getPitch(), preUpdate.isOnGround()));
                } else if (hasMoved) {
                    this.sendQueue.addToSendQueue(new C03PacketPlayer.C04PacketPlayerPosition(preUpdate.getX(), preUpdate.getY(), preUpdate.getZ(), preUpdate.isOnGround()));
                } else if (hasRotated) {
                    this.sendQueue.addToSendQueue(new C03PacketPlayer.C05PacketPlayerLook(preUpdate.getYaw(), preUpdate.getPitch(), preUpdate.isOnGround()));
                } else {
                    this.sendQueue.addToSendQueue(new C03PacketPlayer(preUpdate.isOnGround()));
                }
            } else {
                this.sendQueue.addToSendQueue(new C03PacketPlayer.C06PacketPlayerPosLook(localPlayer.motionX, -999.0, localPlayer.motionZ, preUpdate.getYaw(), preUpdate.getPitch(), preUpdate.isOnGround()));
                hasMoved = false;
            }

            ++this.positionUpdateTicks;
            if (hasMoved) {
                this.lastReportedPosX = preUpdate.getX();
                this.lastReportedPosY = preUpdate.getY();
                this.lastReportedPosZ = preUpdate.getZ();
                this.positionUpdateTicks = 0;
            }

            if (hasRotated) {
                this.lastReportedYaw = preUpdate.getYaw();
                this.lastReportedPitch = preUpdate.getPitch();
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
