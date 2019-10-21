package pl.fernikq.core.listener.block;

import org.bukkit.Bukkit;
import org.bukkit.Material;
import org.bukkit.block.Block;
import org.bukkit.event.EventHandler;
import org.bukkit.event.EventPriority;
import org.bukkit.event.Listener;
import org.bukkit.event.block.BlockIgniteEvent;
import org.bukkit.event.block.BlockSpreadEvent;
import pl.fernikq.core.CorePlugin;
import pl.fernikq.core.region.RegionFeedback;

public class BlockIgniteListener implements Listener {

    private final CorePlugin plugin;

    public BlockIgniteListener(CorePlugin plugin){
        this.plugin = plugin;
        Bukkit.getPluginManager().registerEvents(this, plugin);
    }

    @EventHandler(priority = EventPriority.LOWEST, ignoreCancelled = true)
    public void onSpread(BlockIgniteEvent event){
        Block block = event.getBlock();
        RegionFeedback regionFeedback = this.plugin.getRegionManager().canSpreadFire(block.getLocation());
        if(!regionFeedback.isPermit()){
            event.setCancelled(true);
            return;
        }
    }
}
