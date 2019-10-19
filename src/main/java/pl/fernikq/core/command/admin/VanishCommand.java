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

public class VanishCommand extends CustomCommand {

    private final CorePlugin plugin;

    public VanishCommand(String name, String[] aliases, UserGroup group, CorePlugin plugin){
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
            if(this.plugin.getVanishManager().isVanished(player)){
                this.plugin.getVanishManager().show(player);
                this.plugin.getVanishManager().removeVanished(player);
                this.plugin.getTagManager().updateTag(player);
                return ChatUtil.sendMessage(sender, "&8>> {n}Znow jestes &bwidoczny&8!");
            }
            this.plugin.getVanishManager().hide(player);
            this.plugin.getVanishManager().addVanished(player);
            this.plugin.getTagManager().updateTag(player);
            return ChatUtil.sendMessage(sender, "&8>> {n}Jestes &bniewidzialny&8!");
        }
        Player target = Bukkit.getPlayerExact(args[0]);
        if(target == null){
            return ChatUtil.sendMessage(sender, Lang.playerOffline);
        }
        if(this.plugin.getVanishManager().isVanished(target)){
            this.plugin.getVanishManager().show(target);
            this.plugin.getVanishManager().removeVanished(target);
            this.plugin.getTagManager().updateTag(target);
            if(!target.equals(sender)){
                ChatUtil.sendMessage(sender, "&8>> {n}Wylaczyles &bvanish {n}graczowi &b"+target.getName());
            }
            return ChatUtil.sendMessage(target, "&8>> {n}Twoj &bvanish {n}zostal wylaczony przez &b"+sender.getName());
        }
        this.plugin.getVanishManager().hide(target);
        this.plugin.getVanishManager().addVanished(target);
        this.plugin.getTagManager().updateTag(target);
        if(!target.equals(sender)){
            ChatUtil.sendMessage(sender, "&8>> {n}Wlaczyles &bvanish {n}graczowi &b"+target.getName());
        }
        return ChatUtil.sendMessage(target, "&8>> {n}Twoj &bvanish {n}zostal wlaczony przez &b"+sender.getName());
    }
}
