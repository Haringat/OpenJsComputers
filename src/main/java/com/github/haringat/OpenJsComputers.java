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

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;

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

        InputStream in = getClass().getResourceAsStream("/assets/openjscomputers/bios.js");
        BufferedReader input = new BufferedReader(new InputStreamReader(in));
        String bios = "";
        String line;
        try {
            while((line = input.readLine()) != null) {
                bios += line + "\n";
            }
            input.close();
        } catch (IOException e) {
            e.printStackTrace();
        }
        Items.registerEEPROM("EEPROM (JavaScript BIOS)", bios.getBytes(), new byte[]{}, true);
    }

    @EventHandler
    public void postInit(FMLPostInitializationEvent event) {
        OpenJsComputers.logger.info("OpenJsComputers is initialized.");
    }

}
