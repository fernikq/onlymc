package pl.fernikq.core.listener.entity;

import org.bukkit.Bukkit;
import org.bukkit.entity.Entity;
import org.bukkit.entity.EntityType;
import org.bukkit.event.EventHandler;
import org.bukkit.event.EventPriority;
import org.bukkit.event.Listener;
import org.bukkit.event.entity.CreatureSpawnEvent;
import org.bukkit.event.entity.EntitySpawnEvent;
import pl.fernikq.core.CorePlugin;
import pl.fernikq.core.region.RegionFeedback;

import java.util.Arrays;
import java.util.List;

public class CreatureSpawnListener implements Listener {

    private final CorePlugin plugin;

    public CreatureSpawnListener(CorePlugin plugin){
        this.plugin = plugin;
        Bukkit.getPluginManager().registerEvents(this, plugin);
    }

    @EventHandler(priority = EventPriority.LOWEST, ignoreCancelled = true)
    public void onCreatureSpawn(CreatureSpawnEvent event){
        Entity entity = event.getEntity();
        RegionFeedback regionFeedback = this.plugin.getRegionManager().allowMobSpawning(entity.getLocation().getBlock().getLocation());
        if(!regionFeedback.isPermit()){
            event.setCancelled(true);
            return;
        }
    }
}
