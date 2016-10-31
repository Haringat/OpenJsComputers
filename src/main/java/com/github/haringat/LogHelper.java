package com.github.haringat;

import org.apache.logging.log4j.Level;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

public class LogHelper {

    private static Logger logger = LogManager.getFormatterLogger(OpenJsComputers.NAME);

    private static void log(Level level, Object object) {
        LogHelper.logger.log(level, String.valueOf(object));
    }

    public static void all(Object object) {
        LogHelper.log(Level.ALL, object);
    }

    public static void debug(Object object) {
        LogHelper.log(Level.DEBUG, object);
    }

    public static void error(Object object) {
        LogHelper.log(Level.ERROR, object);
    }

    public static void fatal(Object object) {
        LogHelper.log(Level.FATAL, object);
    }

    public static void info(Object object) {
        LogHelper.log(Level.INFO, object);
    }

    public static void warn(Object object) {
        LogHelper.log(Level.WARN, object);
    }

}
