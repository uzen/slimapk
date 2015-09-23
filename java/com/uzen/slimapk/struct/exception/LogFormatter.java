package com.uzen.slimapk.struct.exception;

import java.util.logging.Logger;
import java.util.logging.Level;
import java.util.logging.LogRecord;
import java.util.logging.ConsoleHandler;
import java.util.logging.SimpleFormatter;

class LogFormatter extends SimpleFormatter {   

    public void configure(Logger Logger) {
        ConsoleHandler handler = new ConsoleHandler();
        Logger.setUseParentHandlers(false);
        handler.setFormatter(this);
        handler.setLevel(Level.ALL);
        Logger.addHandler(handler);
    }
    
    @Override
    public String format(LogRecord record) {
        StringBuilder builder = new StringBuilder();
        builder.append("[").append(record.getLevel()).append("] ");
        builder.append(formatMessage(record));
        builder.append("\n");
        return builder.toString();
    }
}