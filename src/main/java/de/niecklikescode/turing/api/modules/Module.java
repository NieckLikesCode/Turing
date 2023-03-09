package de.niecklikescode.turing.api.modules;

import lombok.Getter;
import lombok.Setter;
import net.minecraft.client.Minecraft;
import net.minecraft.client.entity.EntityPlayerSP;
import net.minecraftforge.common.MinecraftForge;

public class Module {

    public Module() {
        Info info = getClass().getAnnotation(Info.class);

        this.name = info.name().equals("") ? getClass().getSimpleName() : info.name();
        this.displayName = info.displayName().equals("") ? name : info.displayName();
        this.description = info.description();
        this.category = info.category();
        this.keyBind = info.keyBind();
        this.color = info.color();

        this.toggleable = info.toggleable();
    }

    protected final Minecraft MC = Minecraft.getMinecraft();

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

    private boolean enabled, toggleable;

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

    protected EntityPlayerSP getLocalPlayer() {
        return MC.thePlayer;
    }

    public enum Category {
        COMBAT("Combat"),
        MOVEMENT("Movement"),
        PLAYER("Player"),
        VISUAL("Visual"),
        HUD("HUD");

        @Getter
        private final String displayName;

        Category(String displayName) {
            this.displayName = displayName;
        }
    }

}
