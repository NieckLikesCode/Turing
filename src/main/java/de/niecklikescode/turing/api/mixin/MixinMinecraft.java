package de.niecklikescode.turing.api.mixin;

import de.niecklikescode.turing.api.events.MouseClickEvent;
import de.niecklikescode.turing.api.events.TickEvent;
import de.niecklikescode.turing.api.events.KeyDownEvent;
import net.minecraft.client.Minecraft;
import net.minecraftforge.common.MinecraftForge;
import org.lwjgl.input.Keyboard;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

@Mixin(Minecraft.class)
public class MixinMinecraft {

    @Inject(method = "runTick()V", at = @At(value = "INVOKE", target = "Lnet/minecraft/client/Minecraft;dispatchKeypresses()V", shift = At.Shift.AFTER))
    private void onKey(CallbackInfo ci) {
        if (Keyboard.getEventKeyState() && Minecraft.getMinecraft().currentScreen == null) {
            int keyCode = Keyboard.getEventKey() == 0 ? Keyboard.getEventCharacter() + 256 : Keyboard.getEventKey();

            MinecraftForge.EVENT_BUS.post(new KeyDownEvent(keyCode));
        }
    }

    @Inject(method = "clickMouse", at=@At(value = "INVOKE", target = "Lnet/minecraft/client/entity/EntityPlayerSP;swingItem()V"), cancellable = true)
    public void clickMouse(CallbackInfo ci) {
        MouseClickEvent event = new MouseClickEvent();
        MinecraftForge.EVENT_BUS.post(event);

        if(event.isCanceled()) ci.cancel();
    }

    @Inject(method = "runTick", at=@At("HEAD"))
    public void runTick(CallbackInfo ci) {
        MinecraftForge.EVENT_BUS.post(new TickEvent());
    }

}
