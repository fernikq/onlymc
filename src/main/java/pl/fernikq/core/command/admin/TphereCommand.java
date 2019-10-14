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

public class TphereCommand extends CustomCommand {

    private final CorePlugin plugin;

    public TphereCommand(String name, String[] aliases, UserGroup group, CorePlugin plugin) {
        super(name, aliases, group, plugin);
        this.plugin = plugin;
    }

    @Override
    public boolean onCommand(CommandSender sender, Command command, String label, String[] args) {
        if(!(sender instanceof Player)){
            return ChatUtil.sendMessage(sender, Lang.mustBePlayer);
        }
        if(args.length < 1){
            return ChatUtil.sendMessage(sender, MessagesManager.usage("/tphere <gracz>"));
        }
        Player player = (Player)sender;
        Player target = Bukkit.getPlayerExact(args[0]);
        if(target == null){
            return ChatUtil.sendMessage(sender, Lang.playerOffline);
        }
        if(target.equals(player)){
            return ChatUtil.sendMessage(sender, MessagesManager.error("Nie mozesz przeteleportowac siebie!"));
        }
        target.teleport(player);
        ChatUtil.sendMessage(target, "&8>> {n}Zostales przeteleportowany do {c}"+player.getName());
        return ChatUtil.sendMessage(player, "&8>> {n}Przeteleportowales do siebie gracza {c}"+target.getName());
    }
}
