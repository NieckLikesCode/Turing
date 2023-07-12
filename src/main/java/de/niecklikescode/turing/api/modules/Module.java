package de.niecklikescode.turing.api.modules;

import de.niecklikescode.turing.api.utils.RenderUtils;
import lombok.Getter;
import lombok.Setter;
import net.minecraft.client.Minecraft;
import net.minecraft.client.entity.EntityPlayerSP;
import net.minecraft.client.multiplayer.WorldClient;
import net.minecraftforge.common.MinecraftForge;

public class Module {

    public Module() {
        Info info = getClass().getAnnotation(Info.class);

        this.name = info.name().equals("") ? getClass().getSimpleName() : info.name();
        this.displayName = info.displayName().equals("") ? name : info.displayName();
        this.description = info.description();
        this.category = info.category();
        this.keyBind = info.keyBind();
        this.color = info.color() == -1 ? RenderUtils.getColorByString(name) : info.color();

        this.toggleable = info.toggleable();
    }

    protected final static Minecraft MC = Minecraft.getMinecraft();

    @Getter
    private final String name, description;

    @Setter
    @Getter
    private String displayName;

    @Getter
    private final Category category;

    @Setter
    @Getter
    private int keyBind, color;

    @Getter
    private boolean enabled;
    @Getter
    private final boolean toggleable;

    public void enableMod() {}
    public void disableMod() {}

    public void toggle() {
        if(!enabled) {
            enableMod();
            MinecraftForge.EVENT_BUS.register(this);
        } else {
            disableMod();
            MinecraftForge.EVENT_BUS.unregister(this);
        }

        enabled = !enabled;
    }

    protected static EntityPlayerSP getLocalPlayer() {
        return MC.thePlayer;
    }
    protected static WorldClient getWorld() { return MC.theWorld; }

    public enum Category {
        COMBAT("Combat"),
        MOVEMENT("Movement"),
        PLAYER("Player"),
        VISUAL("Visual"),
        HUD("HUD"),
        MISC("MISC");

        @Getter
        private final String displayName;

        Category(String displayName) {
            this.displayName = displayName;
        }
    }

}
