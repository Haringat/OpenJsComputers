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
import li.cil.oc.api.FileSystem;
import org.lwjgl.util.Color;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.util.concurrent.Callable;

@SuppressWarnings("WeakerAccess")
@Mod(
        modid = OpenJsComputers.MODID,
        version = OpenJsComputers.VERSION,
        dependencies = "required-after:OpenComputers",
        acceptedMinecraftVersions = "1.7.*"
)
public class OpenJsComputers {

    public static final String MODID = "${OJSC_MODID}";
    public static final String NAME = "${OJSC_NAME}";
    public static final String VERSION = "${OJSC_VERSION}";
    @Instance(OpenJsComputers.MODID)
    public static OpenJsComputers instance;

    @EventHandler
    public void preInit(FMLPreInitializationEvent event) {
        LogHelper.info("OpenJsComputers version " + OpenJsComputers.VERSION + " is preinitializing...");
    }

    @EventHandler
    public void init(FMLInitializationEvent event) {
        LogHelper.info("OpenJsComputers is initializing...");
        Machine.add(V8Architecture.class);

        InputStream in = getClass().getResourceAsStream("/assets/OpenJsComputers/bios.js");
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
        int blue = Color.BLUE.getRedByte() << 16 | Color.BLUE.getGreenByte() << 8 | Color.BLUE.getBlueByte();
        Items.registerFloppy("JSOS (JavaScript OS)", blue, new Callable<li.cil.oc.api.fs.FileSystem>() {
            @Override
            public li.cil.oc.api.fs.FileSystem call() throws Exception {
                return FileSystem.fromClass(OpenJsComputers.class, OpenJsComputers.MODID, "jsos");
            }
        });
    }

    @EventHandler
    public void postInit(FMLPostInitializationEvent event) {
        LogHelper.info("OpenJsComputers is initialized.");
    }

}
