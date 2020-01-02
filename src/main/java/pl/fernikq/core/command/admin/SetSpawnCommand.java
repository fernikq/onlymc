package pl.fernikq.core.command.admin;

import org.bukkit.Bukkit;
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

public class SetSpawnCommand extends CustomCommand {

    private final CorePlugin plugin;

    public SetSpawnCommand(String name, String[] aliases, UserGroup group, CorePlugin plugin){
        super(name, aliases, group, plugin);
        this.plugin = plugin;
    }

    @Override
    public boolean onCommand(CommandSender sender, Command command, String label, String[] args) {
        if(!(sender instanceof Player)) {
            return ChatUtil.sendMessage(sender, Lang.mustBePlayer);
        }
        Player player = (Player) sender;
        ConfigManager.spawnLocation = LocationUtil.locationToString(player.getLocation());
        player.getWorld().setSpawnLocation(player.getLocation().getBlockX(), player.getLocation().getBlockY(), player.getLocation().getBlockZ());
        Bukkit.setSpawnRadius(0);
        player.getWorld().save();
        this.plugin.getConfigManager().save();
        this.plugin.getRegionManager().reloadBorder(player.getWorld());
        return ChatUtil.sendMessage(sender, "&8>> {n}Pomyslnie ustawiles {c}spawn&8!");
    }
}
