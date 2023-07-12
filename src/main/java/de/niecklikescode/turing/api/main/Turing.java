package de.niecklikescode.turing.api.main;

import de.niecklikescode.turing.api.modules.ModuleManager;
import lombok.Getter;
import net.minecraftforge.fml.common.Mod;
import net.minecraftforge.fml.common.event.FMLInitializationEvent;
import net.minecraftforge.fml.common.event.FMLPreInitializationEvent;
import org.apache.logging.log4j.Logger;

@Mod(modid = "turing", version = "0.1.1", name = "Turing")
public class Turing {

    @Getter
    private static Logger logger;

    @Getter
    private static Turing instance;

    @Mod.EventHandler
    public void preInit(FMLPreInitializationEvent event) {
        logger = event.getModLog();
        instance = this;
    }

    @Mod.EventHandler
    public void init(FMLInitializationEvent event) throws Exception {
        new ModuleManager();
    }

}
