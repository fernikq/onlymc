package pl.fernikq.core.listener.player;

import org.bukkit.Bukkit;
import org.bukkit.Material;
import org.bukkit.block.Block;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.EventPriority;
import org.bukkit.event.Listener;
import org.bukkit.event.block.Action;
import org.bukkit.event.player.PlayerInteractEvent;
import pl.fernikq.core.CorePlugin;
import pl.fernikq.core.region.RegionFeedback;
import pl.fernikq.core.util.ChatUtil;

@SuppressWarnings("deprecation")
public class PlayerInteractListener implements Listener {

    private final CorePlugin plugin;

    public PlayerInteractListener(CorePlugin plugin){
        this.plugin = plugin;
        Bukkit.getPluginManager().registerEvents(this, plugin);
    }

    @EventHandler(priority = EventPriority.LOWEST, ignoreCancelled = true)
    public void onRegion(PlayerInteractEvent event){
        Player player = event.getPlayer();
        Block block = event.getClickedBlock();
        RegionFeedback regionFeedback;
        regionFeedback = this.plugin.getRegionManager().canSpawnVehicles(player, block.getLocation());
        if(!regionFeedback.isPermit()){
            event.setCancelled(true);
            ChatUtil.sendMessage(player, regionFeedback.getFeedbackMessage());
            return;
        }
        if(block.getTypeId() == 60 && event.getAction() == Action.PHYSICAL){
            regionFeedback = this.plugin.getRegionManager().canDestroyFarmlands(block.getLocation());
            if(!regionFeedback.isPermit()){
                event.setCancelled(true);
                return;
            }
        }
    }
}
