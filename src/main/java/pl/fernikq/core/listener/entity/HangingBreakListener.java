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
import pl.fernikq.core.user.User;

public class HangingBreakListener implements Listener {

    private final CorePlugin plugin;

    public HangingBreakListener(CorePlugin plugin){
        this.plugin = plugin;
        Bukkit.getPluginManager().registerEvents(this, plugin);
    }

    @EventHandler(priority = EventPriority.LOWEST, ignoreCancelled = true)
    public void onRemove(HangingBreakEvent event){
        Entity entity = event.getEntity();
        if(event.getCause() == HangingBreakEvent.RemoveCause.ENTITY){
            Entity remover = ((HangingBreakByEntityEvent)event).getRemover();
            if(remover.getType() == EntityType.PLAYER){
                Player player = (Player)remover;
                if(entity.getType() == EntityType.PAINTING){
                    User user = this.plugin.getUserManager().getUser(player.getUniqueId()).getOrNull();
                    RegionFeedback regionFeedback = this.plugin.getRegionManager().canChangePaintings(user, entity.getLocation(), true);
                    if(!regionFeedback.isPermit()){
                        event.setCancelled(true);
                        return;
                    }
                    return;
                }
                if(entity.getType() == EntityType.ITEM_FRAME){
                    User user = this.plugin.getUserManager().getUser(player.getUniqueId()).getOrNull();
                    RegionFeedback regionFeedback = this.plugin.getRegionManager().canChangeFrames(user, entity.getLocation(), true);
                    if(!regionFeedback.isPermit()){
                        event.setCancelled(true);
                        return;
                    }
                    return;
                }
            }else{
                if(entity.getType() == EntityType.PAINTING){
                    RegionFeedback regionFeedback = this.plugin.getRegionManager().canChangePaintings(null, entity.getLocation(), false);
                    if(!regionFeedback.isPermit()){
                        event.setCancelled(true);
                        return;
                    }
                    return;
                }
                if(entity.getType() == EntityType.ITEM_FRAME){
                    RegionFeedback regionFeedback = this.plugin.getRegionManager().canChangeFrames(null, entity.getLocation(), false);
                    if(!regionFeedback.isPermit()){
                        event.setCancelled(true);
                        return;
                    }
                    return;
                }
            }
            return;
        }
        if(event.getCause() == HangingBreakEvent.RemoveCause.EXPLOSION){
            if(entity.getType() == EntityType.PAINTING){
                RegionFeedback regionFeedback = this.plugin.getRegionManager().canChangePaintings(null, entity.getLocation(), false);
                if(!regionFeedback.isPermit()){
                    event.setCancelled(true);
                    return;
                }
                return;
            }
            if(entity.getType() == EntityType.ITEM_FRAME){
                RegionFeedback regionFeedback = this.plugin.getRegionManager().canChangeFrames(null, entity.getLocation(), false);
                if(!regionFeedback.isPermit()){
                    event.setCancelled(true);
                    return;
                }
                return;
            }
        }
    }
}
