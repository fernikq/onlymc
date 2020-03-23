package pl.fernikq.core.listener.entity;

import com.google.common.cache.Cache;
import com.google.common.cache.CacheBuilder;
import org.bukkit.Bukkit;
import org.bukkit.Location;
import org.bukkit.Material;
import org.bukkit.block.Block;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.block.BlockRedstoneEvent;
import pl.fernikq.core.CorePlugin;

import java.util.HashMap;
import java.util.Map;
import java.util.concurrent.TimeUnit;

public class BlockRedstoneListener implements Listener {

    private final CorePlugin plugin;

    private Cache<Location, Integer> redstoneTicks;

    public BlockRedstoneListener(CorePlugin plugin){
        this.plugin = plugin;
        Bukkit.getPluginManager().registerEvents(this, plugin);
        this.redstoneTicks = CacheBuilder.newBuilder().expireAfterWrite(500, TimeUnit.MILLISECONDS).build();
    }

    @EventHandler
    public void onRedstone(BlockRedstoneEvent event){
        Location location = event.getBlock().getLocation();
        Block block = event.getBlock();
        if(block.getType() != Material.REDSTONE_WIRE){
            return;
        }
        int ticks = addAndReturnTicks(location);
        if(ticks > 150){
            block.setType(Material.AIR);
            this.redstoneTicks.asMap().remove(location);
        }
    }

    private int addAndReturnTicks(Location location){
        if(!this.redstoneTicks.asMap().containsKey(location)){
            this.redstoneTicks.put(location, 1);
            return 1;
        }
        int ticks = this.redstoneTicks.asMap().get(location);
        this.redstoneTicks.asMap().replace(location, ticks+1);
        return ticks+1;
    }
}
