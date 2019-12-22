package pl.fernikq.core.command.player;

import org.bukkit.command.Command;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;
import pl.fernikq.core.CorePlugin;
import pl.fernikq.core.command.CustomCommand;
import pl.fernikq.core.config.ConfigManager;
import pl.fernikq.core.config.Lang;
import pl.fernikq.core.config.MessagesManager;
import pl.fernikq.core.user.User;
import pl.fernikq.core.user.UserGroup;
import pl.fernikq.core.util.ChatUtil;

public class EnderChestCommand extends CustomCommand {

    private final CorePlugin plugin;

    public EnderChestCommand(String name, String[] aliases, UserGroup group, CorePlugin plugin){
        super(name, aliases, group, plugin);
        this.plugin = plugin;
    }

    @Override
    public boolean onCommand(CommandSender sender, Command command, String label, String[] args) {
        if(!(sender instanceof Player)) {
            return ChatUtil.sendMessage(sender, Lang.mustBePlayer);
        }
        Player player = (Player)sender;
        if(args.length == 0){
            this.plugin.getUserManager().getUser(player.getUniqueId()).peek(user -> {
                player.openInventory(user.getEnderchest().getInventory());
                user.getEnderchest().setUserEnderchest(user);
            });
        }else{
            User userSender = this.plugin.getUserManager().getUser(player.getUniqueId()).get();
            if(!userSender.canByGroup(UserGroup.MOD)){
                return ChatUtil.sendMessage(sender, MessagesManager.commandErrorPermission);
            }
            this.plugin.getUserManager().getUser(args[0]).peek(user -> {
                player.openInventory(user.getEnderchest().getInventory());
                userSender.getEnderchest().setUserEnderchest(user);
            }).onEmpty(() -> ChatUtil.sendMessage(player, Lang.userNotExists));
        }
        return true;
    }
}
