package pl.fernikq.core.listener.block;

import org.bukkit.Bukkit;
import org.bukkit.Material;
import org.bukkit.block.Block;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.EventPriority;
import org.bukkit.event.Listener;
import org.bukkit.event.block.BlockIgniteEvent;
import org.bukkit.event.block.BlockSpreadEvent;
import pl.fernikq.core.CorePlugin;
import pl.fernikq.core.region.RegionFeedback;
import pl.fernikq.core.region.RegionProtectionType;
import pl.fernikq.core.user.User;

public class BlockIgniteListener implements Listener {

    private final CorePlugin plugin;

    public BlockIgniteListener(CorePlugin plugin){
        this.plugin = plugin;
        Bukkit.getPluginManager().registerEvents(this, plugin);
    }

    @EventHandler(priority = EventPriority.LOWEST, ignoreCancelled = true)
    public void onIgnite(BlockIgniteEvent event){
        Block block = event.getBlock();
        Player player = event.getPlayer();
        if(player != null){
            User user = this.plugin.getUserManager().getUser(player.getUniqueId()).getOrNull();
            RegionFeedback regionFeedback = this.plugin.getRegionManager().can(user, block.getLocation(), RegionProtectionType.CAN_FIRE_SPREAD);
            if(!regionFeedback.isPermit()){
                event.setCancelled(true);
                return;
            }
            return;
        }
        RegionFeedback regionFeedback = this.plugin.getRegionManager().can(block.getLocation(), RegionProtectionType.CAN_FIRE_SPREAD);
        if(!regionFeedback.isPermit()){
            event.setCancelled(true);
            return;
        }
    }
}
