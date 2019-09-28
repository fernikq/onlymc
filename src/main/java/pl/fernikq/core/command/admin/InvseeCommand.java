package pl.fernikq.core.command.admin;

import org.bukkit.Bukkit;
import org.bukkit.command.Command;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;
import pl.fernikq.core.CorePlugin;
import pl.fernikq.core.command.CustomCommand;
import pl.fernikq.core.config.Lang;
import pl.fernikq.core.config.MessagesManager;
import pl.fernikq.core.user.UserGroup;
import pl.fernikq.core.util.ChatUtil;

public class InvseeCommand extends CustomCommand {

    private final CorePlugin plugin;

    public InvseeCommand(String name, String[] aliases, UserGroup group, CorePlugin plugin){
        super(name, aliases, group, plugin);
        this.plugin = plugin;
    }

    @Override
    public boolean onCommand(CommandSender sender, Command command, String label, String[] args) {
        if(!(sender instanceof Player)) {
            return ChatUtil.sendMessage(sender, Lang.mustBePlayer);
        }
        if(args.length < 1) {
            return ChatUtil.sendMessage(sender, MessagesManager.usage("/invsee <nick>"));
        }
        Player target = Bukkit.getPlayerExact(args[0]);
        if(target == null){
            return ChatUtil.sendMessage(sender, Lang.playerOffline);
        }
        Player player = (Player)sender;
        player.openInventory(target.getInventory());
        return true;
    }
}
