package pl.fernikq.core.command.admin;

import org.apache.commons.lang.StringUtils;
import org.bukkit.Bukkit;
import org.bukkit.command.Command;
import org.bukkit.command.CommandSender;
import pl.fernikq.core.CorePlugin;
import pl.fernikq.core.command.CustomCommand;
import pl.fernikq.core.config.MessagesManager;
import pl.fernikq.core.user.UserGroup;
import pl.fernikq.core.util.ChatUtil;

public class BroadcastCommand extends CustomCommand {

    private final CorePlugin plugin;

    public BroadcastCommand(String name, String[] aliases, UserGroup group, CorePlugin plugin) {
        super(name, aliases, group, plugin);
        this.plugin = plugin;
    }

    @Override
    public boolean onCommand(CommandSender sender, Command command, String label, String[] args) {
        if(args.length < 1){
            return ChatUtil.sendMessage(sender, MessagesManager.usage("/bc <wiadomosc>"));
        }
        String message = StringUtils.join(args, " ", 0, args.length);
        Bukkit.getOnlinePlayers().forEach(online -> ChatUtil.sendMessage(online, MessagesManager.broadcastMessage.replace("{MESSAGE}", message)));
        return true;
    }
}
