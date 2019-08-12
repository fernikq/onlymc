package pl.fernikq.core.util;

import org.bukkit.Bukkit;

import java.util.logging.Level;

public class Logger {

    public static void info(String... messages){
        log(Level.INFO, messages);
    }

    public static void warning(String... messages){
        log(Level.WARNING, messages);
    }

    private static void log(Level level, String... messages){
        for(String message : messages){
            Bukkit.getLogger().log(level, message);
        }
    }
}
