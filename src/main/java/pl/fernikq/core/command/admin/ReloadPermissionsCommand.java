package pl.fernikq.core.command.admin;

import org.bukkit.Bukkit;
import org.bukkit.command.Command;
import org.bukkit.command.CommandSender;
import pl.fernikq.core.CorePlugin;
import pl.fernikq.core.command.CustomCommand;
import pl.fernikq.core.config.MessagesManager;
import pl.fernikq.core.user.UserGroup;
import pl.fernikq.core.util.ChatUtil;

public class ReloadPermissionsCommand extends CustomCommand {

    private final CorePlugin plugin;

    public ReloadPermissionsCommand(String name, String[] aliases, UserGroup group, CorePlugin plugin) {
        super(name, aliases, group, plugin);
        this.plugin = plugin;
    }

    @Override
    public boolean onCommand(CommandSender sender, Command command, String label, String[] args) {
        if(args.length < 1){
            return ChatUtil.sendMessage(sender, MessagesManager.usage("/reloadpermissions <ranga>"));
        }
        UserGroup group = UserGroup.getByName(args[0]);
        if(group == null){
            return ChatUtil.sendMessage(sender, "&8>> {n}Dostepne rangi&8: "+UserGroup.getPrefixesToString());
        }
        this.plugin.getUserPermissionsManager().reload();
        Bukkit.getOnlinePlayers().forEach(online -> {
            this.plugin.getUserManager().getUser(online.getUniqueId()).filter(user -> user.getGroup().equals(group)).peek(user -> {
               this.plugin.getUserPermissionsManager().reloadPermissions(user);
            });
        });
        return ChatUtil.sendMessage(sender, "&8>> {n}Pomyslnie przeladowales permisje dla rangi {c}"+group.name());
    }
}
