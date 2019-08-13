package pl.fernikq.core.util;

import org.bukkit.ChatColor;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;

public class ChatUtil {

    public static String fixColor(String message){
        message = StringUtil.replace(message, "{c}", "&3");
        message = StringUtil.replace(message, "{n}", "&f");
        message = StringUtil.replace(message, ">>", "»");
        message = StringUtil.replace(message, "<<", "«");
        message = StringUtil.replace(message, "<3", "♥");
        return ChatColor.translateAlternateColorCodes('&', message);
    }

    public static boolean sendMessage(CommandSender sender, String... messages){
        if(sender instanceof Player){
            for(String message : messages){
                sender.sendMessage(fixColor(message));
            }
        }else{
            for(String message : messages){
                sender.sendMessage(message);
            }
        }
        return true;
    }
}
