package com.github.haringat;


import cpw.mods.fml.common.FMLLog;
import org.apache.logging.log4j.Level;

public class LogHelper {

    private String modName;

    public LogHelper(String modName) {
        this.modName = modName;
    }

    private void log(Level level, Object ...more) {
        Object[] additionalParams = new Object[more.length - 1];
        System.arraycopy(more, 1, additionalParams, 0, more.length - 1);
        FMLLog.log(this.modName, level, String.valueOf(more[0]), additionalParams);
    }

    public void all(Object ...more) {
        this.log(Level.ALL, more);
    }

    public void debug(Object ...more) {
        this.log(Level.DEBUG, more);
    }

    public void error(Object ...more) {
        this.log(Level.ERROR, more);
    }

    public void fatal(Object ...more) {
        this.log(Level.FATAL, more);
    }

    public void info(Object ...more) {
        this.log(Level.INFO, more);
    }

    public void warn(Object ...more) {
        this.log(Level.WARN, more);
    }

}
