package com.uzen.slimapk.struct.exception;

import java.util.logging.Logger;
import java.util.logging.Level;
import com.uzen.slimapk.struct.exception.LogFormatter;

public class Log {
		
    private static Logger Logger;
    
    private static boolean debug = false;
	
    public Log(String className) {
        Logger = Logger.getLogger(className);
        LogFormatter Formatter = new LogFormatter();
        Formatter.configure(Logger);        
        Logger.setLevel(Level.ALL);
    }	

    public static void isLoggable() {
        debug = true;
    }
    
    public static void d(String message, Object param1) {
        if (debug)
            Logger.log(Level.FINE, message, param1);
    }

    public static void e(String message, Object param1) {
        Logger.log(Level.SEVERE, message, param1);
    }

    public static void i(String message, Object param1) {
        Logger.log(Level.INFO, message, param1);
    }

    public static void w(String message, Object param1) {
        Logger.log(Level.WARNING, message, param1);
    }

    public static void d(String message) {
        if (debug)
            Logger.log(Level.FINE, message);
    }

    public static void e(String message) {
        Logger.log(Level.SEVERE, message);
    }

    public static void i(String message) {
        Logger.log(Level.INFO, message);
    }

    public static void w(String message) {
        Logger.log(Level.WARNING, message);
    }
}