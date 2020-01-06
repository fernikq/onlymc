package pl.fernikq.core.command.player;

import org.bukkit.command.Command;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;
import pl.fernikq.core.CorePlugin;
import pl.fernikq.core.command.CustomCommand;
import pl.fernikq.core.config.Lang;
import pl.fernikq.core.user.UserGroup;
import pl.fernikq.core.util.ChatUtil;

public class SidebarCommand extends CustomCommand {

    private final CorePlugin plugin;

    public SidebarCommand(String name, String[] aliases, UserGroup group, CorePlugin plugin){
        super(name, aliases, group, plugin);
        this.plugin = plugin;
    }

    @Override
    public boolean onCommand(CommandSender sender, Command command, String label, String[] args) {
        if(!(sender instanceof Player)) {
            return ChatUtil.sendMessage(sender, Lang.mustBePlayer);
        }
        Player player = (Player)sender;
        this.plugin.getUserManager().getUser(player.getUniqueId()).peek(user -> {
            if(user.getSidebar().isEnabled()){
                user.getSidebar().setEnabled(false);
                user.getSidebar().remove();
                ChatUtil.sendMessage(player, "&8>> {n}Wylaczyles sidebar!");
            }else{
                user.getSidebar().setEnabled(true);
                user.getSidebar().create();
                ChatUtil.sendMessage(player, "&8>> {n}Wlaczyles sidebar!");
            }
        });
        return true;
    }
}
