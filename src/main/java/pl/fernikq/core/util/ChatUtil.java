package pl.fernikq.core.util;

import org.bukkit.ChatColor;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;

import java.util.List;

public class ChatUtil {

    public static String fixColor(String message){
        message = StringUtil.replace(message, "{c}", "&3");
        message = StringUtil.replace(message, "{n}", "&f");
        message = StringUtil.replace(message, ">>", "»");
        message = StringUtil.replace(message, "<<", "«");
        message = StringUtil.replace(message, "<3", "♥");
        return ChatColor.translateAlternateColorCodes('&', message);
    }

    public static List<String> fixColor(List<String> list) {
        for (int i = 0; i < list.size(); i++) {
            String string = (String)list.get(i);
            list.set(i, fixColor(string));
        }
        return list;
    }

    public static boolean sendMessage(CommandSender sender, String... messages){
        if(sender == null || messages == null){
            return true;
        }
        if(sender instanceof Player){
            if(!((Player)sender).isOnline()){
                return true;
            }
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
