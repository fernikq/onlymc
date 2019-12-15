package pl.fernikq.core.listener.entity;

import org.bukkit.Bukkit;
import org.bukkit.entity.Entity;
import org.bukkit.entity.EntityType;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.EventPriority;
import org.bukkit.event.Listener;
import org.bukkit.event.hanging.HangingPlaceEvent;
import pl.fernikq.core.CorePlugin;
import pl.fernikq.core.region.RegionFeedback;
import pl.fernikq.core.user.User;

public class HangingPlaceListener implements Listener {

    private final CorePlugin plugin;

    public HangingPlaceListener(CorePlugin plugin){
        this.plugin = plugin;
        Bukkit.getPluginManager().registerEvents(this, plugin);
    }

    @EventHandler(priority = EventPriority.LOWEST, ignoreCancelled = true)
    public void onPlace(HangingPlaceEvent event) {
        Entity entity = event.getEntity();
        Player player = event.getPlayer();
        if(entity.getType() == EntityType.PAINTING) {
            User user = this.plugin.getUserManager().getUser(player.getUniqueId()).getOrNull();
            RegionFeedback regionFeedback = this.plugin.getRegionManager().canChangePaintings(user, entity.getLocation(), true);
            if(!regionFeedback.isPermit()) {
                event.setCancelled(true);
                return;
            }
            return;
        }
        if(entity.getType() == EntityType.ITEM_FRAME) {
            User user = this.plugin.getUserManager().getUser(player.getUniqueId()).getOrNull();
            RegionFeedback regionFeedback = this.plugin.getRegionManager().canChangeFrames(user, entity.getLocation(), true);
            if(!regionFeedback.isPermit()) {
                event.setCancelled(true);
                return;
            }
            return;
        }
    }
}
