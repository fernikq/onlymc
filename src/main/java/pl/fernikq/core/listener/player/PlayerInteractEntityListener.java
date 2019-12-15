package pl.fernikq.core.listener.player;

import org.bukkit.Bukkit;
import org.bukkit.entity.Entity;
import org.bukkit.entity.EntityType;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.EventPriority;
import org.bukkit.event.Listener;
import org.bukkit.event.player.PlayerInteractEntityEvent;
import pl.fernikq.core.CorePlugin;
import pl.fernikq.core.region.RegionFeedback;
import pl.fernikq.core.user.User;

public class PlayerInteractEntityListener implements Listener {

    private final CorePlugin plugin;

    public PlayerInteractEntityListener(CorePlugin plugin){
        this.plugin = plugin;
        Bukkit.getPluginManager().registerEvents(this, plugin);
    }

    @EventHandler(priority = EventPriority.LOWEST, ignoreCancelled = true)
    public void onRegion(PlayerInteractEntityEvent event){
        Player player = event.getPlayer();
        Entity entity = event.getRightClicked();
        if(entity.getType() != EntityType.ITEM_FRAME){
            return;
        }
        User user = this.plugin.getUserManager().getUser(player.getUniqueId()).getOrNull();
        RegionFeedback regionFeedback = this.plugin.getRegionManager().canChangeFrames(user, entity.getLocation(), true);
        if(!regionFeedback.isPermit()) {
            event.setCancelled(true);
            return;
        }
    }
}
