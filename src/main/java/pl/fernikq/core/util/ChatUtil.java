package pl.fernikq.core.util;

import org.bukkit.ChatColor;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;

import java.util.Arrays;
import java.util.List;
import java.util.concurrent.ThreadLocalRandom;

public class ChatUtil {

    private static List<String> colors = Arrays.asList("&a", "&b", "&c", "&e", "&2", "&3", "&4", "&6", "&f");

    public static String fixColor(String message){
        message = StringUtil.replace(message, "{c}", "&c");
        message = StringUtil.replace(message, "{n}", "&f");
        message = StringUtil.replace(message, ">>", "»");
        message = StringUtil.replace(message, "<<", "«");
        message = StringUtil.replace(message, "<3", "♥");
        return ChatColor.translateAlternateColorCodes('&', message);
    }

    public static String stripColor(String message){
        message = StringUtil.replace(message, "&c", "{c}");
        message = StringUtil.replace(message, "{f}", "{n}");
        message = StringUtil.replace(message, "»", ">>");
        message = StringUtil.replace(message, "«", "<<");
        message = StringUtil.replace(message, "♥", "<3");
        return ChatColor.stripColor(message);
    }

    public static String getRainbowString(String string, boolean bolt){
        StringBuilder stringBuilder = new StringBuilder(string.length());
        string.chars().forEach(value -> stringBuilder.append(colors.get(ThreadLocalRandom.current().nextInt(colors.size()))+(bolt ? "&l" : "")).append((char)value));
        return stringBuilder.toString();
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
