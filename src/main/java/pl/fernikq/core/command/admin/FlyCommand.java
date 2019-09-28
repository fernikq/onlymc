package pl.fernikq.core.command.admin;

import org.bukkit.Bukkit;
import org.bukkit.command.Command;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;
import pl.fernikq.core.CorePlugin;
import pl.fernikq.core.command.CustomCommand;
import pl.fernikq.core.config.Lang;
import pl.fernikq.core.user.UserGroup;
import pl.fernikq.core.util.ChatUtil;

public class FlyCommand extends CustomCommand {

    private final CorePlugin plugin;

    public FlyCommand(String name, String[] aliases, UserGroup group, CorePlugin plugin){
        super(name, aliases, group, plugin);
        this.plugin = plugin;
    }

    @Override
    public boolean onCommand(CommandSender sender, Command command, String label, String[] args) {
        if(args.length == 0){
            if(!(sender instanceof Player)){
                return ChatUtil.sendMessage(sender, Lang.mustBePlayer);
            }
            Player player = (Player)sender;
            if(player.getAllowFlight()){
                player.setAllowFlight(!player.getAllowFlight());
                return ChatUtil.sendMessage(sender, "&8>> {n}Twoj fly zostal &cwylaczony");
            }
            player.setAllowFlight(!player.getAllowFlight());
            return ChatUtil.sendMessage(sender, "&8>> {n}Twoj fly zostal &awlaczony");
        }
        Player target = Bukkit.getPlayerExact(args[0]);
        if(target == null){
            return ChatUtil.sendMessage(sender, Lang.playerOffline);
        }
        if(target.getAllowFlight()){
            target.setAllowFlight(!target.getAllowFlight());
            if(!target.equals(sender)){
                ChatUtil.sendMessage(sender, "&8>> &cWylaczyles {n}fly gracza &c"+target.getName());
            }
            return ChatUtil.sendMessage(target, "&8>> {n}Twoj fly zostal &cwylaczony {n}przez &c"+sender.getName());
        }

        target.setAllowFlight(!target.getAllowFlight());
        if(!target.equals(sender)){
            ChatUtil.sendMessage(sender, "&8>> &aWlaczyles {n}fly gracza &a"+target.getName());
        }
        return ChatUtil.sendMessage(target, "&8>> {n}Twoj fly zostal &awlaczony {n}przez &a"+sender.getName());
    }
}
