package pl.fernikq.core.listener.player;

import org.bukkit.Bukkit;
import org.bukkit.entity.EntityType;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.EventPriority;
import org.bukkit.event.Listener;
import org.bukkit.event.entity.FoodLevelChangeEvent;
import pl.fernikq.core.CorePlugin;
import pl.fernikq.core.region.RegionFeedback;

public class PlayerFoodLevelListener implements Listener {

    private final CorePlugin plugin;

    public PlayerFoodLevelListener(CorePlugin plugin) {
        this.plugin = plugin;
        Bukkit.getPluginManager().registerEvents(this, plugin);
    }

    @EventHandler(priority = EventPriority.LOWEST, ignoreCancelled = true)
    public void onFood(FoodLevelChangeEvent event){
        if(event.getEntity().getType() != EntityType.PLAYER){
            return;
        }
        Player player = (Player)event.getEntity();
        RegionFeedback regionFeedback = this.plugin.getRegionManager().canBeHungry(player.getLocation().getBlock().getLocation());
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
