package com.github.haringat;

import com.github.haringat.oc.v8.V8Architecture;
import cpw.mods.fml.common.event.FMLPostInitializationEvent;
import cpw.mods.fml.common.event.FMLPreInitializationEvent;
import cpw.mods.fml.common.Mod;
import cpw.mods.fml.common.Mod.Instance;
import cpw.mods.fml.common.Mod.EventHandler;
import cpw.mods.fml.common.event.FMLInitializationEvent;
import li.cil.oc.api.Items;
import li.cil.oc.api.Machine;

@SuppressWarnings("WeakerAccess")
@Mod(
        modid = OpenJsComputers.MODID,
        version = OpenJsComputers.VERSION,
        dependencies = "required-after:OpenComputers",
        acceptedMinecraftVersions = "1.7.*"
)
public class OpenJsComputers {

    public static LogHelper logger = new LogHelper(OpenJsComputers.NAME);
    public static final String MODID = "${OJSC_MODID}";
    public static final String NAME = "${OJSC_NAME}";
    public static final String VERSION = "${OJSC_VERSION}";
    @Instance(OpenJsComputers.MODID)
    public static OpenJsComputers instance;

    @EventHandler
    public void preInit(FMLPreInitializationEvent event) {
        OpenJsComputers.logger.info("OpenJsComputers version " + OpenJsComputers.VERSION + " is preinitializing...");
    }

    @EventHandler
    public void init(FMLInitializationEvent event) {
        OpenJsComputers.logger.info("OpenJsComputers is initializing...");
        Machine.add(V8Architecture.class);
        Items.registerEEPROM("EEPROM (JavaScript BIOS)", "(function(){})();".getBytes(), new byte[]{}, true);
    }

    @EventHandler
    public void postInit(FMLPostInitializationEvent event) {
        OpenJsComputers.logger.info("OpenJsComputers is initialized.");
    }

}
