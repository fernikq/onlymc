package pl.fernikq.core.command.admin;

import org.bukkit.command.Command;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;
import pl.fernikq.core.CorePlugin;
import pl.fernikq.core.command.CustomCommand;
import pl.fernikq.core.config.Lang;
import pl.fernikq.core.config.MessagesManager;
import pl.fernikq.core.user.UserGroup;
import pl.fernikq.core.util.ChatUtil;

public class DelWarpCommand extends CustomCommand {

    private final CorePlugin plugin;

    public DelWarpCommand(String name, String[] aliases, UserGroup group, CorePlugin plugin){
        super(name, aliases, group, plugin);
        this.plugin = plugin;
    }

    @Override
    public boolean onCommand(CommandSender sender, Command command, String label, String[] args) {
        if(!(sender instanceof Player)){
            return ChatUtil.sendMessage(sender, Lang.mustBePlayer);
        }
        if(args.length < 1){
            return ChatUtil.sendMessage(sender, MessagesManager.usage("/delwarp <nazwa>"));
        }
        this.plugin.getWarpManager().getWarp(args[0]).peek(warp -> {
            this.plugin.getWarpManager().removeWarp(warp);
            ChatUtil.sendMessage(sender, "&8>> {n}Usunales warp o nazwie {c}"+warp.getName());
            return;
        }).onEmpty(() -> ChatUtil.sendMessage(sender, MessagesManager.error("Podany warp nie istnieje!")));
        return true;
    }
}
