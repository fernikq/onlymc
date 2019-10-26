package pl.fernikq.core.listener.entity;

import org.bukkit.Bukkit;
import org.bukkit.entity.Entity;
import org.bukkit.entity.EntityType;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.EventPriority;
import org.bukkit.event.Listener;
import org.bukkit.event.entity.EntityDamageByEntityEvent;
import pl.fernikq.core.CorePlugin;
import pl.fernikq.core.region.RegionFeedback;
import pl.fernikq.core.region.RegionProtectionType;
import pl.fernikq.core.util.ChatUtil;
import pl.fernikq.core.util.PlayerUtil;

public class EntityDamageByEntityListener implements Listener {

    private final CorePlugin plugin;

    public EntityDamageByEntityListener(CorePlugin plugin){
        this.plugin = plugin;
        Bukkit.getPluginManager().registerEvents(this, plugin);
    }

    @EventHandler(priority = EventPriority.LOWEST, ignoreCancelled = true)
    public void onRegion(EntityDamageByEntityEvent event){
        if(event.getEntity().getType() != EntityType.PLAYER){
            return;
        }
        Player damager = PlayerUtil.getDamager(event);
        if(damager == null){
            return;
        }
        Player victim = (Player)event.getEntity();
        RegionFeedback regionFeedback = this.plugin.getRegionManager().canHurt(damager, victim);
        if(!regionFeedback.isPermit()){
            event.setCancelled(true);
            ChatUtil.sendMessage(damager, regionFeedback.getFeedbackMessage());
            return;
        }
    }

    @EventHandler(priority = EventPriority.LOWEST, ignoreCancelled = true)
    public void onFrame(EntityDamageByEntityEvent event) {
        if(event.getEntity().getType() != EntityType.ITEM_FRAME){
            return;
        }
        Player damager = PlayerUtil.getDamager(event);
        if(damager == null) {
            return;
        }
        RegionFeedback regionFeedback = this.plugin.getRegionManager().can(damager, event.getEntity().getLocation(), RegionProtectionType.FRAMES);
        if(!regionFeedback.isPermit()) {
            event.setCancelled(true);
            return;
        }
    }
}
