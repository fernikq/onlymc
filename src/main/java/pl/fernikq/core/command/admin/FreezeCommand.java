package pl.fernikq.core.command.admin;

import org.apache.commons.lang.StringUtils;
import org.bukkit.Bukkit;
import org.bukkit.command.Command;
import org.bukkit.command.CommandSender;
import pl.fernikq.core.CorePlugin;
import pl.fernikq.core.command.CustomCommand;
import pl.fernikq.core.config.ConfigManager;
import pl.fernikq.core.config.MessagesManager;
import pl.fernikq.core.user.UserGroup;
import pl.fernikq.core.util.ChatUtil;

public class FreezeCommand extends CustomCommand {

    private final CorePlugin plugin;

    public FreezeCommand(String name, String[] aliases, UserGroup group, CorePlugin plugin) {
        super(name, aliases, group, plugin);
        this.plugin = plugin;
    }

    @Override
    public boolean onCommand(CommandSender sender, Command command, String label, String[] args) {
        if(args.length < 1){
            return ChatUtil.sendMessage(sender, MessagesManager.usage("/freeze <true/false>"));
        }
        boolean freezeValue = Boolean.parseBoolean(args[0]);
        ConfigManager.freeze = freezeValue;
        this.plugin.getConfigManager().save();
        ChatUtil.sendMessage(sender, freezeValue ? "&8[&b&lZamrozenie&8] &fzostalo &awlaczone" : "&8[&b&lZamrozenie&8] &fzostalo &cwylaczone");
        return true;
    }
}
