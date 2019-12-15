package pl.fernikq.core.listener.player;

import org.bukkit.Bukkit;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.EventPriority;
import org.bukkit.event.Listener;
import org.bukkit.event.player.PlayerDropItemEvent;
import org.bukkit.event.player.PlayerPickupItemEvent;
import pl.fernikq.core.CorePlugin;

public class PlayerPickupItemListener implements Listener {

    private final CorePlugin plugin;

    public PlayerPickupItemListener(CorePlugin plugin) {
        this.plugin = plugin;
        Bukkit.getPluginManager().registerEvents(this, plugin);
    }

    @EventHandler(priority = EventPriority.LOWEST, ignoreCancelled = true)
    public void onPickupItem(PlayerPickupItemEvent event){
        Player player = event.getPlayer();
        if(this.plugin.getVanishManager().isVanished(player)){
            event.setCancelled(true);
            return;
        }
    }
}
