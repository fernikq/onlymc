package pl.fernikq.core.command.player;

import org.bukkit.Location;
import org.bukkit.command.Command;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;
import pl.fernikq.core.CorePlugin;
import pl.fernikq.core.command.CustomCommand;
import pl.fernikq.core.config.ConfigManager;
import pl.fernikq.core.config.Lang;
import pl.fernikq.core.user.UserGroup;
import pl.fernikq.core.util.ChatUtil;
import pl.fernikq.core.util.LocationUtil;

public class SpawnCommand extends CustomCommand {

    private final CorePlugin plugin;

    public SpawnCommand(String name, String[] aliases, UserGroup group, CorePlugin plugin){
        super(name, aliases, group, plugin);
        this.plugin = plugin;
    }

    @Override
    public boolean onCommand(CommandSender sender, Command command, String label, String[] args) {
        if(!(sender instanceof Player)) {
            return ChatUtil.sendMessage(sender, Lang.mustBePlayer);
        }
        Player player = (Player) sender;
        Location spawn = LocationUtil.locationFromString(ConfigManager.spawnLocation);
        this.plugin.getTeleportManager().teleportToLocation(player, spawn, ConfigManager.teleportSpawnTime, "spawn");
        return true;
    }
}
