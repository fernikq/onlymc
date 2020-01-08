package pl.fernikq.core.command.admin;

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

public class BackupCommand extends CustomCommand {

    private final CorePlugin plugin;

    public BackupCommand(String name, String[] aliases, UserGroup group, CorePlugin plugin){
        super(name, aliases, group, plugin);
        this.plugin = plugin;
    }

    @Override
    public boolean onCommand(CommandSender sender, Command command, String label, String[] args) {
        if(!(sender instanceof Player)) {
            return ChatUtil.sendMessage(sender, Lang.mustBePlayer);
        }
        Player player = (Player)sender;
        if(args.length < 1){
            return ChatUtil.sendMessage(player, MessagesManager.usage("/backup <nick>"));
        }
        User user = this.plugin.getUserManager().getUser(player.getUniqueId()).get();
        this.plugin.getUserManager().getUser(args[0]).peek(backupUser -> {
            if(backupUser.getBackups().isEmpty()){
                ChatUtil.sendMessage(player, MessagesManager.error("Podany gracz nie posiada backupow!"));
                return;
            }
            this.plugin.getUserInventory().playerBackups(user, backupUser).openInventory(player);
        }).onEmpty(() -> ChatUtil.sendMessage(player, Lang.userNotExists));
        return true;
    }
}
