package pl.fernikq.core.command.player;

import org.bukkit.command.Command;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;
import pl.fernikq.core.CorePlugin;
import pl.fernikq.core.command.CustomCommand;
import pl.fernikq.core.config.ConfigManager;
import pl.fernikq.core.config.Lang;
import pl.fernikq.core.config.MessagesManager;
import pl.fernikq.core.user.UserGroup;
import pl.fernikq.core.util.ChatUtil;
import pl.fernikq.core.util.TeleportManager;

public class WarpCommand extends CustomCommand {

    private final CorePlugin plugin;

    public WarpCommand(String name, String[] aliases, UserGroup group, CorePlugin plugin){
        super(name, aliases, group, plugin);
        this.plugin = plugin;
    }

    @Override
    public boolean onCommand(CommandSender sender, Command command, String label, String[] args) {
        if(!(sender instanceof Player)){
            return ChatUtil.sendMessage(sender, Lang.mustBePlayer);
        }
        if(args.length < 1){
           ChatUtil.sendMessage(sender, MessagesManager.usage("/warp <nazwa>"));
           return ChatUtil.sendMessage(sender, "&8>> {n}Dostpne warpy&8: "+this.plugin.getWarpManager().getWarpsToString());
        }
        if(this.plugin.getWarpManager().getWarps().isEmpty()){
            return ChatUtil.sendMessage(sender, MessagesManager.error("Na serwerze nie ma zadnych warpow!"));
        }
        this.plugin.getWarpManager().getWarp(args[0]).peek(warp -> {
            if(!this.plugin.getWarpManager().canTeleport(this.plugin.getUserManager().getUser(((Player)sender).getUniqueId()).get(), warp)){
                ChatUtil.sendMessage(sender, "&8>> {n}Aby przeteleportowac sie na ten warp potrzebujesz range "+warp.getRequiredGroup().getPrefix()+" {n}lub wyzsza!");
                return;
            }
            this.plugin.getTeleportManager().teleportToLocation((Player)sender, warp.getLocation(), ConfigManager.teleportWarpTime, "warp "+warp.getName());
            return;
        }).onEmpty(() -> {
           ChatUtil.sendMessage(sender, MessagesManager.error("Podany warp nie istnieje!"));
           ChatUtil.sendMessage(sender, "&8>> {n}Dostpne warpy&8: "+this.plugin.getWarpManager().getWarpsToString());
           return;
        });
        return true;
    }
}
