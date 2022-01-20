package pl.fernikq.core.command.guild.player;

import org.bukkit.command.Command;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;
import pl.fernikq.core.CorePlugin;
import pl.fernikq.core.command.CustomCommand;
import pl.fernikq.core.config.ConfigManager;
import pl.fernikq.core.config.Lang;
import pl.fernikq.core.config.MessagesManager;
import pl.fernikq.core.user.UserGroup;
import pl.fernikq.core.util.ChatUtil;
import pl.fernikq.core.util.TimeUtil;

public class GuildItemsCommand extends CustomCommand {

    private final CorePlugin plugin;

    public GuildItemsCommand(String name, String[] aliases, UserGroup group, CorePlugin plugin){
        super(name, aliases, group, plugin);
        this.plugin = plugin;
    }

    @Override
    public boolean onCommand(CommandSender sender, Command command, String label, String[] args) {
        if(!(sender instanceof Player)) {
            return ChatUtil.sendMessage(sender, Lang.mustBePlayer);
        }
        if(args.length < 1) {
            return ChatUtil.sendMessage(sender, MessagesManager.usage("/g itemy"));
        }
        Player player = (Player) sender;
        if(ConfigManager.guildCreateBlockTime > System.currentTimeMillis()){
            return ChatUtil.sendMessage(player, MessagesManager.error("Zakladanie gildii zablokowane jest jeszcze przez "+ TimeUtil.getTimeToString(ConfigManager.guildCreateBlockTime - System.currentTimeMillis())));
        }
        this.plugin.getUserManager().getUser(player.getUniqueId()).peek(user -> this.plugin.getGuildInventory().guildItems(user).openInventory(player));
        return true;
    }
}
