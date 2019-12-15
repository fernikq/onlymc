package pl.fernikq.core.listener.player;

import org.bukkit.Bukkit;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.EventPriority;
import org.bukkit.event.Listener;
import org.bukkit.event.player.PlayerDropItemEvent;
import pl.fernikq.core.CorePlugin;

public class PlayerDropItemListener implements Listener {

    private final CorePlugin plugin;

    public PlayerDropItemListener(CorePlugin plugin) {
        this.plugin = plugin;
        Bukkit.getPluginManager().registerEvents(this, plugin);
    }

    @EventHandler(priority = EventPriority.LOWEST, ignoreCancelled = true)
    public void onDropItem(PlayerDropItemEvent event){
        Player player = event.getPlayer();
        if(this.plugin.getVanishManager().isVanished(player)){
            event.setCancelled(true);
            return;
        }
    }
}
