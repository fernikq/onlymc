package pl.fernikq.core.listener.block;

import org.bukkit.Bukkit;
import org.bukkit.block.Block;
import org.bukkit.event.EventHandler;
import org.bukkit.event.EventPriority;
import org.bukkit.event.Listener;
import org.bukkit.event.block.BlockBurnEvent;
import org.bukkit.event.block.BlockSpreadEvent;
import pl.fernikq.core.CorePlugin;
import pl.fernikq.core.region.RegionFeedback;
import pl.fernikq.core.region.RegionProtectionType;

public class BlockSpreadListener implements Listener {

    private final CorePlugin plugin;

    public BlockSpreadListener(CorePlugin plugin){
        this.plugin = plugin;
        Bukkit.getPluginManager().registerEvents(this, plugin);
    }

    @EventHandler(priority = EventPriority.LOWEST, ignoreCancelled = true)
    public void onSpread(BlockSpreadEvent event){
        Block block = event.getBlock();
        RegionFeedback regionFeedback = this.plugin.getRegionManager().can(block.getLocation(), RegionProtectionType.FIRE_SPREAD);
        if(!regionFeedback.isPermit()){
            event.setCancelled(true);
            return;
        }
    }
}
