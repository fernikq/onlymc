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
import pl.fernikq.core.CorePlugin;
import pl.fernikq.core.region.RegionFeedback;
import pl.fernikq.core.util.ChatUtil;

public class HangingBreakListener implements Listener {

    private final CorePlugin plugin;

    public HangingBreakListener(CorePlugin plugin){
        this.plugin = plugin;
        Bukkit.getPluginManager().registerEvents(this, plugin);
    }

    @EventHandler(priority = EventPriority.LOWEST, ignoreCancelled = true)
    public void onRemove(HangingBreakEvent event){
        Entity entity = event.getEntity();
        if(event instanceof HangingBreakByEntityEvent) {
            HangingBreakByEntityEvent entityEvent = (HangingBreakByEntityEvent) event;
            Entity remover = entityEvent.getRemover();
            if(remover.getType() == EntityType.PLAYER){
                Player player = (Player)remover;
                if(entity.getType() == EntityType.PAINTING){
                    RegionFeedback regionFeedback = this.plugin.getRegionManager().canDestroyPaintings(player, entity.getLocation());
                    if(!regionFeedback.isPermit()){
                        event.setCancelled(true);
                        return;
                    }
                    return;
                }
                if(entity.getType() == EntityType.ITEM_FRAME){
                    RegionFeedback regionFeedback = this.plugin.getRegionManager().canDestroyFrames(player, entity.getLocation());
                    if(!regionFeedback.isPermit()){
                        event.setCancelled(true);
                        return;
                    }
                    return;
                }
            }
        }
        if(entity.getType() == EntityType.PAINTING){
            RegionFeedback regionFeedback = this.plugin.getRegionManager().canDestroyPaintings(entity.getLocation());
            if(!regionFeedback.isPermit()){
                event.setCancelled(true);
                return;
            }
            return;
        }
        if(entity.getType() == EntityType.ITEM_FRAME){
            RegionFeedback regionFeedback = this.plugin.getRegionManager().canDestroyFrames(entity.getLocation());
            if(!regionFeedback.isPermit()){
                event.setCancelled(true);
                return;
            }
            return;
        }
    }
}
