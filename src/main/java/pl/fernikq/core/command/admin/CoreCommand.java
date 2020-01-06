package pl.fernikq.core.command.admin;

import org.bukkit.command.Command;
import org.bukkit.command.CommandSender;
import pl.fernikq.core.CorePlugin;
import pl.fernikq.core.command.CustomCommand;
import pl.fernikq.core.config.Lang;
import pl.fernikq.core.config.MessagesManager;
import pl.fernikq.core.user.UserGroup;
import pl.fernikq.core.util.ChatUtil;

public class CoreCommand extends CustomCommand {

    private final CorePlugin plugin;

    public CoreCommand(String name, String[] aliases, UserGroup group, CorePlugin plugin) {
        super(name, aliases, group, plugin);
        this.plugin = plugin;
    }

    @Override
    public boolean onCommand(CommandSender sender, Command command, String label, String[] args) {
        if(args.length < 1){
            return ChatUtil.sendMessage(sender, MessagesManager.usage("/core <reload>"));
        }
        if(args[0].equalsIgnoreCase("reload")){
            this.plugin.getConfigManager().reload();
            this.plugin.getMessagesManager().reload();
            this.plugin.getAutoMessageManager().reload();
            this.plugin.getKitManager().reload();
            this.plugin.getSimpleCommandManager().reload();
            this.plugin.getRegionManager().reload();
            this.plugin.getGeneratorManager().reload();
            this.plugin.getShopManager().reload();
            this.plugin.getDropManager().reload();
            this.plugin.getUserPermissionsManager().reload();
            this.plugin.getUserManager().getOnlineUsers().stream().filter(user -> user.getSidebar().isEnabled()).forEach(user -> {
                user.getSidebar().create();
            });
            return ChatUtil.sendMessage(sender, "&8>> {c}Core {n}zostal przeladowany&8!");
        }
        return ChatUtil.sendMessage(sender, MessagesManager.usage("/core <reload>"));
    }
}
