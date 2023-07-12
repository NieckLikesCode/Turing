package de.niecklikescode.turing.api.mixin;

import de.niecklikescode.turing.api.modules.ModuleManager;
import de.niecklikescode.turing.modules.Velocity;
import net.minecraft.client.Minecraft;
import net.minecraft.entity.Entity;
import net.minecraft.network.play.server.S12PacketEntityVelocity;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

import java.util.Random;

@Mixin(net.minecraft.client.network.NetHandlerPlayClient.class)
public class MixinNetHandler {

    // TODO: Properly implement settings
    private final double horizontal = 0.5, vertical = 1, chance = 0.8;

    private final Minecraft mc = Minecraft.getMinecraft();
    private final Random random = new Random();

    @Inject(method = "handleEntityVelocity", at = @At(value = "INVOKE", target = "Lnet/minecraft/entity/Entity;setVelocity(DDD)V"), cancellable = true)
    public void handleEntityVelocity(S12PacketEntityVelocity packetIn, CallbackInfo ci) {
        Entity entity = mc.theWorld.getEntityByID(packetIn.getEntityID());

        if(!ModuleManager.getModule(Velocity.class).isEnabled()) return;
        if(random.nextDouble() > chance) return;

        // Local player velocity change
        if(entity != null && entity == mc.thePlayer) {
            ci.cancel();

            entity.setVelocity(
                    (double)packetIn.getMotionX() / 8000.0 * horizontal,
                    (double)packetIn.getMotionY() / 8000.0 * vertical,
                    (double)packetIn.getMotionZ() / 8000.0 * horizontal
            );
        }
    }

}
