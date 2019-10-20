package pl.fernikq.core.listener.player;

import org.bukkit.Bukkit;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.EventPriority;
import org.bukkit.event.Listener;
import org.bukkit.event.entity.EntityDamageEvent;
import pl.fernikq.core.CorePlugin;
import pl.fernikq.core.region.RegionFeedback;
import pl.fernikq.core.util.ChatUtil;

public class PlayerDamageListener implements Listener {

    private final CorePlugin plugin;

    public PlayerDamageListener(CorePlugin plugin){
        this.plugin = plugin;
        Bukkit.getPluginManager().registerEvents(this, plugin);
    }

    @EventHandler(priority = EventPriority.LOWEST, ignoreCancelled = true)
    public void onGodMode(EntityDamageEvent event){
        if(!(event.getEntity() instanceof Player)){
            return;
        }
        Player player = (Player)event.getEntity();
        RegionFeedback regionFeedback = this.plugin.getRegionManager().canHurt(player);
        if(!regionFeedback.isPermit()){
            event.setCancelled(true);
            return;
        }
        this.plugin.getUserManager().getUser(player.getUniqueId()).peek(user -> {
           if(user.isGodMode()){
               event.setCancelled(true);
           }
        });
    }
}
