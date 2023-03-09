package de.niecklikescode.turing.api.mixin;

import de.niecklikescode.turing.api.main.Turing;
import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.GuiMainMenu;
import net.minecraft.client.gui.ScaledResolution;
import net.minecraftforge.fml.common.Mod;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

@Mixin(GuiMainMenu.class)
public class MixinGuiMainMenu {

    private final Minecraft minecraft = Minecraft.getMinecraft();

    @Inject(method = "drawScreen", at = @At("RETURN"))
    public void drawScreen(int mouseX, int mouseY, float partialTicks, CallbackInfo ci) {
        ScaledResolution scaledResolution = new ScaledResolution(minecraft);
        Mod modInfo = Turing.class.getAnnotation(Mod.class);

        String text = "§b" + modInfo.name() + " " + Turing.class.getAnnotation(Mod.class).version();

        minecraft.fontRendererObj.drawStringWithShadow(
                text,
                scaledResolution.getScaledWidth() - minecraft.fontRendererObj.getStringWidth(text) - 2,
                scaledResolution.getScaledHeight() - minecraft.fontRendererObj.FONT_HEIGHT - 12,
                    0xFFFFFF
                );
    }

}
