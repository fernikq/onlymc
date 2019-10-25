package pl.fernikq.core.listener.entity;

import org.bukkit.Bukkit;
import org.bukkit.entity.Entity;
import org.bukkit.entity.EntityType;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.EventPriority;
import org.bukkit.event.Listener;
import org.bukkit.event.hanging.HangingBreakByEntityEvent;
import org.bukkit.event.hanging.HangingBreakEvent;
import org.bukkit.event.hanging.HangingPlaceEvent;
import pl.fernikq.core.CorePlugin;
import pl.fernikq.core.region.RegionFeedback;

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
            RegionFeedback regionFeedback = this.plugin.getRegionManager().canChangePaintings(player, entity.getLocation());
            if(!regionFeedback.isPermit()) {
                event.setCancelled(true);
                return;
            }
        }
        if(entity.getType() == EntityType.ITEM_FRAME) {
            RegionFeedback regionFeedback = this.plugin.getRegionManager().canChangeFrames(player, entity.getLocation());
            if(!regionFeedback.isPermit()) {
                event.setCancelled(true);
                return;
            }
        }
    }
}
