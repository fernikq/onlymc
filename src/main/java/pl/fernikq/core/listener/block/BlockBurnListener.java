package pl.fernikq.core.listener.block;

import org.bukkit.Bukkit;
import org.bukkit.Material;
import org.bukkit.block.Block;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.EventPriority;
import org.bukkit.event.Listener;
import org.bukkit.event.block.BlockBurnEvent;
import org.bukkit.event.block.BlockIgniteEvent;
import org.bukkit.event.block.BlockSpreadEvent;
import pl.fernikq.core.CorePlugin;
import pl.fernikq.core.region.RegionFeedback;
import pl.fernikq.core.util.ChatUtil;

public class BlockBurnListener implements Listener {

    private final CorePlugin plugin;

    public BlockBurnListener(CorePlugin plugin){
        this.plugin = plugin;
        Bukkit.getPluginManager().registerEvents(this, plugin);
    }

    @EventHandler(priority = EventPriority.LOWEST, ignoreCancelled = true)
    public void onBurn(BlockBurnEvent event){
        Block block = event.getBlock();
        RegionFeedback regionFeedback = this.plugin.getRegionManager().allowFireSpread(block.getLocation());
        if(!regionFeedback.isPermit()){
            event.setCancelled(true);
            return;
        }
    }
}
