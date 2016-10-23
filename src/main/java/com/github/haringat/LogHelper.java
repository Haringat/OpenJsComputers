package com.github.haringat;

import org.apache.logging.log4j.Level;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

public class LogHelper {

    private Logger logger;

    public LogHelper(String modName) {
        this.logger = LogManager.getFormatterLogger(modName);
    }

    private void log(Level level, Object object) {
        this.logger.log(level, String.valueOf(object));
    }

    public void all(Object object) {
        this.log(Level.ALL, object);
    }

    public void debug(Object object) {
        this.log(Level.DEBUG, object);
    }

    public void error(Object object) {
        this.log(Level.ERROR, object);
    }

    public void fatal(Object object) {
        this.log(Level.FATAL, object);
    }

    public void info(Object object) {
        this.log(Level.INFO, object);
    }

    public void warn(Object object) {
        this.log(Level.WARN, object);
    }

}
