package pl.fernikq.core.listener.block;

import org.bukkit.Bukkit;
import org.bukkit.block.Block;
import org.bukkit.event.EventHandler;
import org.bukkit.event.EventPriority;
import org.bukkit.event.Listener;
import org.bukkit.event.block.BlockBurnEvent;
import org.bukkit.event.block.BlockFromToEvent;
import pl.fernikq.core.CorePlugin;
import pl.fernikq.core.listener.player.BucketListener;
import pl.fernikq.core.region.RegionFeedback;

public class BlockFromToListener implements Listener {

    private final CorePlugin plugin;

    public BlockFromToListener(CorePlugin plugin){
        this.plugin = plugin;
        Bukkit.getPluginManager().registerEvents(this, plugin);
    }

    @EventHandler(priority = EventPriority.LOWEST, ignoreCancelled = true)
    public void onWaterFlow(BlockFromToEvent event){
        Block block = event.getBlock();
        if(this.plugin.getLocationOfWaterBlocks().containsKey(block.getLocation())){
            event.setCancelled(true);
        }
    }
}
