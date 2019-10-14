package pl.fernikq.core.command.admin;

import org.bukkit.Bukkit;
import org.bukkit.command.Command;
import org.bukkit.command.CommandSender;
import pl.fernikq.core.CorePlugin;
import pl.fernikq.core.command.CustomCommand;
import pl.fernikq.core.config.ConfigManager;
import pl.fernikq.core.config.MessagesManager;
import pl.fernikq.core.user.UserGroup;
import pl.fernikq.core.util.ChatUtil;

public class ChatCommand extends CustomCommand {

    private final CorePlugin plugin;

    public ChatCommand(String name, String[] aliases, UserGroup group, CorePlugin plugin) {
        super(name, aliases, group, plugin);
        this.plugin = plugin;
    }

    @Override
    public boolean onCommand(CommandSender sender, Command command, String label, String[] args) {
        if(args.length < 1){
            return ChatUtil.sendMessage(sender, MessagesManager.usage("/chat <clear, on, off"));
        }
        switch(args[0].toLowerCase()){
            case "on":{
                if(ConfigManager.chatEnabled){
                    return ChatUtil.sendMessage(sender, MessagesManager.error("Chat jest juz wlaczony!"));
                }
                ConfigManager.chatEnabled = true;
                this.plugin.getConfigManager().save();
                Bukkit.getOnlinePlayers().forEach(online -> {
                    ChatUtil.sendMessage(online, " ", "&8>> {n}Chat zostal &awlaczony&8!", " ");
                });
                return true;
            }
            case "off":{
                if(!ConfigManager.chatEnabled){
                    return ChatUtil.sendMessage(sender, MessagesManager.error("Chat jest juz wylaczony!"));
                }
                ConfigManager.chatEnabled = false;
                this.plugin.getConfigManager().save();
                Bukkit.getOnlinePlayers().forEach(online -> {
                    ChatUtil.sendMessage(online, " ", "&8>> {n}Chat zostal &cwylaczony&8!", " ");
                });
                return true;
            }
            case "c":
            case "clear":{
                Bukkit.getOnlinePlayers().forEach(online -> {
                    for(int i = 0; i < 300; i++){
                        ChatUtil.sendMessage(online, " ");
                    }
                    ChatUtil.sendMessage(online, "&8>> {n}Chat zostal {c}wyczyszczony {n}przez {c}"+sender.getName(), " ");
                });
                return true;
            }
            default:{
                return ChatUtil.sendMessage(sender, MessagesManager.usage("/chat <clear, on, off"));
            }
        }
    }
}
