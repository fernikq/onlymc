package pl.fernikq.core.listener.player;

import org.bukkit.Bukkit;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.EventPriority;
import org.bukkit.event.Listener;
import org.bukkit.event.entity.EntityDamageEvent;
import pl.fernikq.core.CorePlugin;
import pl.fernikq.core.config.ConfigManager;
import pl.fernikq.core.region.RegionFeedback;

public class PlayerDamageListener implements Listener {

    private final CorePlugin plugin;

    public PlayerDamageListener(CorePlugin plugin){
        this.plugin = plugin;
        Bukkit.getPluginManager().registerEvents(this, plugin);
    }

    @EventHandler(priority = EventPriority.LOWEST, ignoreCancelled = true)
    public void onHurt(EntityDamageEvent event){
        if(!(event.getEntity() instanceof Player)){
            return;
        }
        Player player = (Player)event.getEntity();
        RegionFeedback regionFeedback = this.plugin.getRegionManager().canHurt(player.getLocation().getBlock().getLocation());
        if(!regionFeedback.isPermit()){
            event.setCancelled(true);
            return;
        }
        this.plugin.getUserManager().getUser(player.getUniqueId()).peek(user -> {
           if(user.isGodMode()){
               event.setCancelled(true);
           }
        });
        if(ConfigManager.freezeTime > System.currentTimeMillis()){
            event.setCancelled(true);
        }
    }

    @EventHandler(priority = EventPriority.HIGHEST)
    public void onDiscoArmor(EntityDamageEvent event){
        if(event.isCancelled()){
            return;
        }
        if(!(event.getEntity() instanceof Player)){
            return;
        }
        Player player = (Player) event.getEntity();
        if(!player.isSneaking()){
            return;
        }
        if(!this.plugin.getDiscoArmorManager().isWorking(player.getUniqueId())){
            return;
        }
        if(this.plugin.getDiscoArmorManager().getOriginalArmor().containsKey(player.getUniqueId())){
            this.plugin.getDiscoArmorManager().restoreOriginalArmor(player);
        }
    }
}
