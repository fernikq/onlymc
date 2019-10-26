package pl.fernikq.core.listener.entity;

import org.bukkit.Bukkit;
import org.bukkit.block.Block;
import org.bukkit.event.EventHandler;
import org.bukkit.event.EventPriority;
import org.bukkit.event.Listener;
import org.bukkit.event.entity.EntityExplodeEvent;
import pl.fernikq.core.CorePlugin;
import pl.fernikq.core.region.RegionFeedback;
import pl.fernikq.core.region.RegionProtectionType;

import java.util.ArrayList;
import java.util.List;

public class EntityExplodeListener implements Listener {

    private final CorePlugin plugin;

    public EntityExplodeListener(CorePlugin plugin){
        this.plugin = plugin;
        Bukkit.getPluginManager().registerEvents(this, plugin);
    }

    @EventHandler(priority = EventPriority.LOWEST, ignoreCancelled = true)
    public void onExplode(EntityExplodeEvent event){
        //TODO godziny
        List<Block> toRemove = new ArrayList<>();
        for(Block block : event.blockList()){
            RegionFeedback regionFeedback = this.plugin.getRegionManager().can(block.getLocation(), RegionProtectionType.EXPLOSION);
            if(!regionFeedback.isPermit()){
                toRemove.add(block);
            }
        }
        toRemove.forEach(block -> event.blockList().remove(block));
    }
}
