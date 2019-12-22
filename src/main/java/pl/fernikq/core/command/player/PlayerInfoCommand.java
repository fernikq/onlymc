package pl.fernikq.core.command.player;

import org.bukkit.command.Command;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;
import pl.fernikq.core.CorePlugin;
import pl.fernikq.core.command.CustomCommand;
import pl.fernikq.core.config.Lang;
import pl.fernikq.core.config.MessagesManager;
import pl.fernikq.core.user.User;
import pl.fernikq.core.user.UserGroup;
import pl.fernikq.core.util.ChatUtil;

public class PlayerInfoCommand extends CustomCommand {

    private final CorePlugin plugin;

    public PlayerInfoCommand(String name, String[] aliases, UserGroup group, CorePlugin plugin){
        super(name, aliases, group, plugin);
        this.plugin = plugin;
    }

    @Override
    public boolean onCommand(CommandSender sender, Command command, String label, String[] args) {
        Player player = (Player) sender;
        User user = this.plugin.getUserManager().getUser(player.getUniqueId()).get();
        if(args.length == 1){
            this.plugin.getUserManager().getUser(args[0]).peek(targetUser -> {
               this.plugin.getUserInventory().playerInfo(user, targetUser).openInventory(player);
            }).onEmpty(() -> ChatUtil.sendMessage(player, Lang.userNotExists));
            return true;
        }
        this.plugin.getUserInventory().playerInfo(user, user).openInventory(player);
        return true;
    }
}
