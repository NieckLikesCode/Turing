package de.niecklikescode.turing.modules;

import de.niecklikescode.turing.api.modules.Info;
import de.niecklikescode.turing.api.modules.Module;
import net.minecraft.client.renderer.GlStateManager;
import net.minecraft.client.renderer.Tessellator;
import net.minecraft.client.renderer.WorldRenderer;
import net.minecraft.client.renderer.vertex.DefaultVertexFormats;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.util.MathHelper;
import net.minecraftforge.client.event.RenderLivingEvent.Specials.Pre;
import net.minecraftforge.fml.common.eventhandler.SubscribeEvent;
import org.lwjgl.input.Keyboard;
import org.lwjgl.opengl.GL11;

@Info(name = "Name Tags", description = "Renders name tags trough walls", keyBind = Keyboard.KEY_B, category = Module.Category.VISUAL)
public class Nametags extends Module {

    @SubscribeEvent
    public void renderLabelEvent(Pre<?> event) {
        if(!(event.entity instanceof EntityPlayer)) return;
        renderNametag(event, -1);
    }

    public void renderNametag(Pre<?> e, double maxDistance) {
        if (e.entity instanceof EntityPlayer && e.entity != MC.thePlayer && e.entity.deathTime == 0 && (getLocalPlayer().getDistanceToEntity(e.entity) <= maxDistance || maxDistance == -1)) {
            EntityPlayer en = (EntityPlayer)e.entity;

            e.setCanceled(true);

            double r = en.getHealth() / en.getMaxHealth();
            String h = (r < 0.3D ? "§c" : (r < 0.5D ? "§6" : (r < 0.7D ? "§e" : "§a"))) + Math.round(en.getHealth());

            // Make the text cursive whenever the target is sneaking
            String str = String.format("%s" + en.getDisplayName().getFormattedText() + " §r(%s§r)", en.isSneaking() ? "§o" : "", h);

            GlStateManager.pushMatrix();
            GlStateManager.translate((float)e.x + 0.0F, (float)e.y + en.height + 0.5F, (float)e.z);
            GL11.glNormal3f(0.0F, 1.0F, 0.0F);
            GlStateManager.rotate(-MC.getRenderManager().playerViewY, 0.0F, 1.0F, 0.0F);
            GlStateManager.rotate(MC.getRenderManager().playerViewX, 1.0F, 0.0F, 0.0F);

            float f1 = scaledDistance(en);
            GlStateManager.scale(-f1, -f1, f1);

            if (en.isSneaking()) {
                GlStateManager.translate(0.0F, 9.374999F, 0.0F);
            }

            GlStateManager.disableLighting();
            GlStateManager.depthMask(false);
            GlStateManager.disableDepth();
            GlStateManager.enableBlend();
            GlStateManager.tryBlendFuncSeparate(770, 771, 1, 0);
            Tessellator tessellator = Tessellator.getInstance();
            WorldRenderer worldrenderer = tessellator.getWorldRenderer();
            int i = 0;
            int j = MC.fontRendererObj.getStringWidth(str) / 2;
            GlStateManager.disableTexture2D();
            worldrenderer.begin(7, DefaultVertexFormats.POSITION_COLOR);
            worldrenderer.pos(-j - 1, -1 + i, 0.0D).color(0.0F, 0.0F, 0.0F, 0.25F).endVertex();
            worldrenderer.pos(-j - 1, 8 + i, 0.0D).color(0.0F, 0.0F, 0.0F, 0.25F).endVertex();
            worldrenderer.pos(j + 1, 8 + i, 0.0D).color(0.0F, 0.0F, 0.0F, 0.25F).endVertex();
            worldrenderer.pos(j + 1, -1 + i, 0.0D).color(0.0F, 0.0F, 0.0F, 0.25F).endVertex();
            tessellator.draw();

            GlStateManager.enableTexture2D();
            MC.fontRendererObj.drawString(str, -MC.fontRendererObj.getStringWidth(str) / 2, i, -1);
            GlStateManager.enableDepth();
            GlStateManager.depthMask(true);
            GlStateManager.enableLighting();
            GlStateManager.disableBlend();
            GlStateManager.color(1.0F, 1.0F, 1.0F, 1.0F);
            GlStateManager.popMatrix();
        }

    }

    private float scaledDistance(EntityPlayer target) {
        float dist = getLocalPlayer().getDistanceToEntity(target);

        return MathHelper.clamp_float(
                dist * 0.002541666875f, // Scaling factor
                0.02666667F, // Normal nametag size
                0.14666667F); // Max nametag size
    }

}
