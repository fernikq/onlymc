package pl.fernikq.core.listener.player;

import org.bukkit.Bukkit;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.EventPriority;
import org.bukkit.event.Listener;
import org.bukkit.event.player.PlayerRespawnEvent;
import pl.fernikq.core.CorePlugin;
import pl.fernikq.core.config.ConfigManager;
import pl.fernikq.core.util.ChatUtil;
import pl.fernikq.core.util.LocationUtil;
import pl.fernikq.core.util.TitleUtil;

public class PlayerRespawnListener implements Listener {

    private final CorePlugin plugin;

    public PlayerRespawnListener(CorePlugin plugin){
        this.plugin = plugin;
        Bukkit.getPluginManager().registerEvents(this, plugin);
    }

    @EventHandler(priority = EventPriority.LOWEST, ignoreCancelled = true)
    public void onRespawn(PlayerRespawnEvent event){
        Player player = event.getPlayer();
        TitleUtil.sendTitle(player, ChatUtil.fixColor("&c&lZginales"), 4);
        TitleUtil.sendSubTitle(player, ChatUtil.fixColor("{n}Nie poddawaj sie!"), 4);
        player.teleport(LocationUtil.locationFromString(ConfigManager.spawnLocation));
        this.plugin.getUserManager().getUser(player.getUniqueId()).peek(user -> {
            user.getSidebar().update();
        });
    }
}
