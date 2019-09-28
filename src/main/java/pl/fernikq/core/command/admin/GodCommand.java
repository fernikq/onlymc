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

public class GodCommand extends CustomCommand {

    private final CorePlugin plugin;

    public GodCommand(String name, String[] aliases, UserGroup group, CorePlugin plugin){
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
            this.plugin.getUserManager().getUser(player.getUniqueId()).peek(user -> {
                if(user.isGodMode()){
                    ChatUtil.sendMessage(sender, "&8>> {n}Twoj god zostal &cwylaczony");
                    user.setGodMode(!user.isGodMode());
                    return;
                }
                ChatUtil.sendMessage(sender, "&8>> {n}Twoj god zostal &awlaczony");
                user.setGodMode(!user.isGodMode());
                return;
            });
            return true;
        }
        Player target = Bukkit.getPlayerExact(args[0]);
        if(target == null){
            return ChatUtil.sendMessage(sender, Lang.playerOffline);
        }
        this.plugin.getUserManager().getUser(target.getUniqueId()).peek(user -> {
            if(user.isGodMode()){
                if(!target.equals(sender)){
                    ChatUtil.sendMessage(sender, "&8>> &cWylaczyles {n}god gracza &c"+target.getName());
                }
                ChatUtil.sendMessage(target, "&8>> {n}Twoj god zostal &cwylaczony {n}przez &c"+sender.getName());
                user.setGodMode(!user.isGodMode());
                return;
            }
            if(!target.equals(sender)){
                ChatUtil.sendMessage(sender, "&8>> &aWlaczyles {n}god gracza &a"+target.getName());
            }
            ChatUtil.sendMessage(target, "&8>> {n}Twoj god zostal &awlaczony {n}przez &a"+sender.getName());
            user.setGodMode(!user.isGodMode());
            return;
        });
        return true;
    }
}
