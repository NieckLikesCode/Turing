package de.niecklikescode.turing.api.modules;

import com.google.common.reflect.ClassPath;
import de.niecklikescode.turing.api.main.Turing;
import de.niecklikescode.turing.api.events.KeyDownEvent;
import de.niecklikescode.turing.api.modules.settings.ModSetting;
import lombok.Getter;
import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.FontRenderer;
import net.minecraft.client.gui.ScaledResolution;
import net.minecraftforge.common.MinecraftForge;
import net.minecraftforge.fml.common.eventhandler.SubscribeEvent;
import net.minecraftforge.fml.common.gameevent.TickEvent;
import org.apache.commons.lang3.reflect.FieldUtils;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Comparator;
import java.util.List;
import java.util.stream.Collectors;

public class ModuleManager {

    @Getter
    private static final ArrayList<Module> mods = new ArrayList<>();

    public static Module getModule(Class<? extends Module> clazz) {
        return mods.stream().filter(m -> m.getClass() == clazz).findFirst().orElse(null);
    }

    @SuppressWarnings("UnstableApiUsage")
    public ModuleManager() throws Exception {
        /*
         * Iterate over every loaded class in module packet and add those extending module and annotated with Info.class
         * to our mod list
         */
        for (ClassPath.ClassInfo classInfo : ClassPath.from(Thread.currentThread().getContextClassLoader())
                .getTopLevelClassesRecursive("de.niecklikescode.turing.modules")) {
            Class<?> clazz = classInfo.load();

            if(clazz.isAnnotationPresent(Info.class)
                && Module.class.isAssignableFrom(clazz)) {

                Module module = (Module) clazz.getConstructors()[0].newInstance();

                // Read all fields filter for annotated ones and add them as settings to the module instance
                Arrays.stream(FieldUtils.getAllFields(clazz))
                        .filter(field -> field.isAnnotationPresent(ModSetting.class))
                                .forEach(field -> {
                                    try {
                                        //module.addSetting((Setting<?>) FieldUtils.readField(field, module));
                                    } catch (Exception e) {
                                        throw new RuntimeException(String.format("Failed to read field %s in %s", field.getName(), module.getName()), e);
                                    }
                                });

                mods.add(module);
            }

        }

        // Register self as event handler
        MinecraftForge.EVENT_BUS.register(this);

        // Compile mod list to log to console
        StringBuilder modList = new StringBuilder();
        mods.forEach(mod -> modList.append(mod.getName()).append(", "));

        Turing.getLogger().info(String.format("Loaded (%d) mods: ", mods.size()) + modList);
    }

    @SubscribeEvent
    public void keyPressed(KeyDownEvent event) {
        mods.stream().filter(mod -> mod.getKeyBind() == event.getKeyCode()).forEach(Module::toggle);
    }

    private final FontRenderer fontRenderer = Minecraft.getMinecraft().fontRendererObj;
    private final Minecraft mc = Minecraft.getMinecraft();

    @SubscribeEvent
    public void renderOverlay(TickEvent.RenderTickEvent event) {
        if(event.phase == TickEvent.Phase.START || mc.currentScreen != null || mc.gameSettings.showDebugInfo) return;

        List<Module> modules = ModuleManager.getMods()
                .stream()
                .sorted(
                        Comparator.comparingInt((Module mod) -> fontRenderer.getStringWidth(mod.getDisplayName())).reversed() // Sort by length first
                                .thenComparing(Module::getDisplayName)) // Sort alphabetically later
                .filter(Module::isEnabled)
                .collect(Collectors.toList());

        ScaledResolution scaledResolution = new ScaledResolution(Minecraft.getMinecraft());

        int y = scaledResolution.getScaledHeight() - fontRenderer.FONT_HEIGHT - 3;
        for (Module module : modules) {
            String name = module.getDisplayName();

            int x = scaledResolution.getScaledWidth() - fontRenderer.getStringWidth(name) - 2;

            fontRenderer.drawStringWithShadow(name, x, y, module.getColor());

            y -= fontRenderer.FONT_HEIGHT + 3;
        }

    }

}
