package de.niecklikescode.turing.api.mixin;

import de.niecklikescode.turing.api.events.PlayerAttackEvent;
import de.niecklikescode.turing.api.modules.ModuleManager;
import de.niecklikescode.turing.modules.Reach;
import net.minecraft.client.Minecraft;
import net.minecraft.client.multiplayer.PlayerControllerMP;
import net.minecraft.entity.Entity;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.item.ItemBlock;
import net.minecraftforge.common.MinecraftForge;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;

import java.util.Random;

@Mixin(PlayerControllerMP.class)
public class MixinPlayerControllerMP {

    @Inject(method = "attackEntity", at = @At("HEAD"), cancellable = true)
    public void preAttack(EntityPlayer playerIn, Entity targetEntity, CallbackInfo ci) {
        PlayerAttackEvent event = new PlayerAttackEvent(targetEntity, PlayerAttackEvent.EventPhase.PRE);
        MinecraftForge.EVENT_BUS.post(event);

        if(event.isCanceled()) ci.cancel();
    }

    @Inject(method = "attackEntity", at = @At("RETURN"), cancellable = true)
    public void postAttack(EntityPlayer playerIn, Entity targetEntity, CallbackInfo ci) {
        PlayerAttackEvent event = new PlayerAttackEvent(targetEntity, PlayerAttackEvent.EventPhase.POST);
        MinecraftForge.EVENT_BUS.post(event);

        if(event.isCanceled()) ci.cancel();
    }

    // Extend reach when the module is enabled
    @Inject(method = "extendedReach", at = @At("HEAD"), cancellable = true)
    public void extendedReach(CallbackInfoReturnable<Boolean> ci) {
        if(ModuleManager.getModule(Reach.class).isEnabled()) ci.setReturnValue(true);
    }

    private final Random random = new Random();
    private final float minReach = 3.1f, maxReach = 3.3f, chance = 0.89f;

    @Inject(method = "getBlockReachDistance", at = @At("HEAD"), cancellable = true)
    public void getBlockReachDistance(CallbackInfoReturnable<Float> ci) {
        if(ModuleManager.getModule(Reach.class).isEnabled() && shouldModifyRange()) {
            if(Math.random() > chance) return;

            ci.setReturnValue(getRandomBetween(minReach, maxReach));
        }
    }

    private float getRandomBetween(float min, float max) {
        return min + random.nextFloat() * (max - min);
    }

    // Reach mod should only be active when the player is fighting, not if he's trying to place blocks
    private boolean shouldModifyRange() {
        return Minecraft.getMinecraft().thePlayer.getHeldItem() != null &&
                !(Minecraft.getMinecraft().thePlayer.getHeldItem().getItem() instanceof ItemBlock);
    }

}
