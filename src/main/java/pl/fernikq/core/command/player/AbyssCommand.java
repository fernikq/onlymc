package pl.fernikq.core.command.player;

import org.bukkit.command.Command;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;
import pl.fernikq.core.CorePlugin;
import pl.fernikq.core.command.CustomCommand;
import pl.fernikq.core.config.Lang;
import pl.fernikq.core.config.MessagesManager;
import pl.fernikq.core.user.UserGroup;
import pl.fernikq.core.util.ChatUtil;

public class AbyssCommand extends CustomCommand {

    private final CorePlugin plugin;

    public AbyssCommand(String name, String[] aliases, UserGroup group, CorePlugin plugin){
        super(name, aliases, group, plugin);
        this.plugin = plugin;
    }

    @Override
    public boolean onCommand(CommandSender sender, Command command, String label, String[] args) {
        if(!(sender instanceof Player)) {
            return ChatUtil.sendMessage(sender, Lang.mustBePlayer);
        }
        Player player = (Player)sender;
        if(!this.plugin.getAbyssManager().isOpened()){
            return ChatUtil.sendMessage(player, MessagesManager.error("Otchlan jest zamknieta!"));
        }
        if(this.plugin.getAbyssManager().getInventoriesToList().isEmpty()){
            return ChatUtil.sendMessage(player, MessagesManager.error("Otchlan jest pusta!"));
        }
        this.plugin.getAbyssManager().getInventoriesToList().get(0).openInventory(player);
        return true;
    }
}
