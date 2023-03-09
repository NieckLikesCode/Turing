package de.niecklikescode.turing.api.modules;

import com.google.common.reflect.ClassPath;
import de.niecklikescode.turing.api.main.Turing;
import de.niecklikescode.turing.api.events.KeyDownEvent;
import de.niecklikescode.turing.api.modules.settings.ModSetting;
import lombok.Getter;
import net.minecraftforge.common.MinecraftForge;
import net.minecraftforge.fml.common.eventhandler.SubscribeEvent;
import org.apache.commons.lang3.reflect.FieldUtils;

import java.util.ArrayList;
import java.util.Arrays;

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
    public void onKeyDown(KeyDownEvent event) {
        mods.stream().filter(mod -> mod.getKeyBind() == event.getKeyCode()).forEach(Module::toggle);
    }


}
