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
import org.bukkit.inventory.ItemStack;
import pl.fernikq.core.CorePlugin;
import pl.fernikq.core.region.RegionFeedback;
import pl.fernikq.core.region.RegionProtectionType;
import pl.fernikq.core.util.ChatUtil;

import java.util.List;
import java.util.Map;

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
                    RegionFeedback regionFeedback = this.plugin.getRegionManager().can(player, entity.getLocation(), RegionProtectionType.PAINTINGS);
                    if(!regionFeedback.isPermit()){
                        event.setCancelled(true);
                        return;
                    }
                    return;
                }
                if(entity.getType() == EntityType.ITEM_FRAME){
                    RegionFeedback regionFeedback = this.plugin.getRegionManager().can(player, entity.getLocation(), RegionProtectionType.FRAMES);
                    if(!regionFeedback.isPermit()){
                        event.setCancelled(true);
                        return;
                    }
                    return;
                }
            }else{
                if(entity.getType() == EntityType.PAINTING){
                    RegionFeedback regionFeedback = this.plugin.getRegionManager().can(entity.getLocation(), RegionProtectionType.PAINTINGS);
                    if(!regionFeedback.isPermit()){
                        event.setCancelled(true);
                        return;
                    }
                    return;
                }
                if(entity.getType() == EntityType.ITEM_FRAME){
                    RegionFeedback regionFeedback = this.plugin.getRegionManager().can(entity.getLocation(), RegionProtectionType.FRAMES);
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
                RegionFeedback regionFeedback = this.plugin.getRegionManager().can(entity.getLocation(), RegionProtectionType.PAINTINGS);
                if(!regionFeedback.isPermit()){
                    event.setCancelled(true);
                    return;
                }
                return;
            }
            if(entity.getType() == EntityType.ITEM_FRAME){
                RegionFeedback regionFeedback = this.plugin.getRegionManager().can(entity.getLocation(), RegionProtectionType.FRAMES);
                if(!regionFeedback.isPermit()){
                    event.setCancelled(true);
                    return;
                }
                return;
            }
        }
    }
}
